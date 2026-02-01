package io.why503.paymentservice.domain.payment.service.impl;

import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.enums.BookingStatus;
import io.why503.paymentservice.domain.booking.service.BookingService;
import io.why503.paymentservice.domain.payment.mapper.PaymentMapper;
import io.why503.paymentservice.domain.payment.model.dto.request.PaymentRequest;
import io.why503.paymentservice.domain.payment.model.dto.response.PaymentResponse;
import io.why503.paymentservice.domain.payment.model.entity.Payment;
import io.why503.paymentservice.domain.payment.model.enums.PaymentMethod;
import io.why503.paymentservice.domain.payment.model.enums.PaymentRefType;
import io.why503.paymentservice.domain.payment.model.enums.PaymentStatus;
import io.why503.paymentservice.domain.payment.repository.PaymentRepository;
import io.why503.paymentservice.domain.payment.service.PaymentService;
import io.why503.paymentservice.domain.point.model.entity.Point;
import io.why503.paymentservice.domain.point.model.enums.PointStatus;
import io.why503.paymentservice.domain.point.service.PointService;
import io.why503.paymentservice.global.client.AccountClient;
import io.why503.paymentservice.global.client.PerformanceClient;
import io.why503.paymentservice.global.client.PgClient;
import io.why503.paymentservice.global.client.dto.request.PointUseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException; // [추가] 404 처리를 위해 필요

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    private final BookingService bookingService;
    private final PointService pointService;

    // 외부 서비스 클라이언트
    private final AccountClient accountClient;
    private final PgClient pgClient;
    private final PerformanceClient performanceClient;

    /**
     * 통합 결제 승인
     * 1. 주문 번호 중복 검사
     * 2. 대상(Booking/Point) 식별 및 분기 처리
     */
    @Override
    @Transactional
    public PaymentResponse pay(Long userSq, PaymentRequest request) {
        // 1. 중복 검사 (이미 처리된 건 -> 409 Conflict 유지)
        if (paymentRepository.findByOrderId(request.orderId()).isPresent()) {
            throw new IllegalStateException("이미 처리된 주문 번호입니다.");
        }

        Payment payment;
        String orderId = request.orderId();

        // 2. 도메인 식별 및 Service 위임 (MSA 규칙 적용)
        if (orderId.startsWith("BOOKING-")) {
            Booking booking = bookingService.findByOrderId(orderId);
            if (booking == null) {
                // [수정] 대상 없음 -> 404 Not Found
                throw new NoSuchElementException("유효하지 않은 예매 주문 번호입니다.");
            }
            payment = processBookingPayment(userSq, booking, request);

        } else if (orderId.startsWith("POINT-")) {
            Point point = pointService.findByOrderId(orderId);
            if (point == null) {
                // [수정] 대상 없음 -> 404 Not Found
                throw new NoSuchElementException("유효하지 않은 포인트 충전 주문 번호입니다.");
            }
            payment = processPointChargePayment(userSq, point, request);

        } else {
            // 잘못된 형식 -> 400 Bad Request 유지
            throw new IllegalArgumentException("지원하지 않는 주문 번호 형식입니다.");
        }

        return paymentMapper.entityToResponse(payment);
    }

    /**
     * [비즈니스 로직 1] 예매(Booking) 결제 처리
     */
    private Payment processBookingPayment(Long userSq, Booking booking, PaymentRequest request) {
        // A. 권한 및 상태 검증
        // [수정] 권한 없음 -> 403 Forbidden
        if (!booking.getUserSq().equals(userSq)) throw new SecurityException("본인 예매만 결제 가능");

        // 상태 충돌 -> 409 Conflict 유지
        if (booking.getStatus() != BookingStatus.PENDING) throw new IllegalStateException("결제 가능 상태 아님");

        // B. 금액 검증 (잘못된 입력 -> 400 Bad Request 유지)
        if (request.amount().longValue() != booking.getFinalAmount().longValue()) {
            throw new IllegalArgumentException("결제 요청 금액과 예매 금액이 불일치합니다.");
        }

        // C. 결제 수단 판별
        long usePoint = request.usePointAmount();
        long pgAmount = request.amount() - usePoint;
        PaymentMethod method = determinePaymentMethod(pgAmount, usePoint);

        // D. 포인트 차감
        if (usePoint > 0) {
            accountClient.decreasePoint(userSq, new PointUseRequest(usePoint));
        }

        // E. PG 결제 승인
        String approvedPgKey = null;
        if (pgAmount > 0) {
            try {
                approvedPgKey = pgClient.approvePayment(request.paymentKey(), request.orderId(), pgAmount);
            } catch (Exception e) {
                if (usePoint > 0) accountClient.increasePoint(userSq, new PointUseRequest(usePoint)); // 롤백
                throw new IllegalStateException("PG 결제 승인 실패: " + e.getMessage());
            }
        }

        // 3. 좌석 확정
        try {
            List<Long> seatIds = getSeatIdsFromBooking(booking);
            performanceClient.confirmRoundSeats(userSq, seatIds);
        } catch (Exception e) {
            if (approvedPgKey != null) pgClient.cancelPayment(approvedPgKey, "시스템 오류: 좌석 확정 실패", pgAmount);
            if (usePoint > 0) accountClient.increasePoint(userSq, new PointUseRequest(usePoint));
            throw new IllegalStateException("좌석 확정 실패. 결제 취소됨.");
        }

        // F. 결제 엔티티 생성 및 저장
        Payment payment = Payment.builder()
                .userSq(userSq)
                .orderId(request.orderId())
                .refType(PaymentRefType.BOOKING)
                .method(method)
                .totalAmount(request.amount())
                .pgAmount(pgAmount)
                .pointAmount(usePoint)
                .build();

        payment.approve(approvedPgKey);
        booking.confirm();
        return paymentRepository.save(payment);
    }

    /**
     * [비즈니스 로직 2] 포인트 충전(Point) 결제 처리
     */
    private Payment processPointChargePayment(Long userSq, Point point, PaymentRequest request) {
        // A. 권한 및 상태 검증
        // [수정] 권한 없음 -> 403 Forbidden
        if (!point.getUserSq().equals(userSq)) {
            throw new SecurityException("본인의 충전 요청만 결제할 수 있습니다.");
        }
        // 상태 충돌 -> 409 Conflict 유지
        if (point.getStatus() != PointStatus.READY) {
            throw new IllegalStateException("충전 가능한 상태가 아닙니다.");
        }

        // B, C. 잘못된 입력 -> 400 Bad Request 유지
        if (request.usePointAmount() > 0) {
            throw new IllegalArgumentException("포인트 충전 시 포인트를 사용할 수 없습니다.");
        }
        if (!request.amount().equals(point.getChargeAmount())) {
            throw new IllegalArgumentException("요청 금액이 충전 신청 금액과 다릅니다.");
        }

        // D. PG 결제 승인
        String approvedPgKey = pgClient.approvePayment(request.paymentKey(), request.orderId(), request.amount());

        // E. 실제 포인트 적립
        accountClient.increasePoint(userSq, new PointUseRequest(point.getChargeAmount()));

        // F. 결제 엔티티 생성
        Payment payment = Payment.builder()
                .userSq(userSq)
                .orderId(request.orderId())
                .refType(PaymentRefType.POINT)
                .method(PaymentMethod.CARD)
                .totalAmount(request.amount())
                .pgAmount(request.amount())
                .pointAmount(0L)
                .build();

        // G. 상태 업데이트
        payment.approve(approvedPgKey);
        point.complete();

        return paymentRepository.save(payment);
    }

    /**
     * 결제 상세 조회
     */
    @Override
    public PaymentResponse findPayment(Long userSq, Long paymentSq) {
        // [수정] 조회 실패 -> 404 Not Found
        Payment payment = paymentRepository.findById(paymentSq)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 결제 내역입니다."));

        // [수정] 권한 없음 -> 403 Forbidden
        if (!payment.getUserSq().equals(userSq)) {
            throw new SecurityException("본인의 결제 내역만 조회할 수 있습니다.");
        }

        return paymentMapper.entityToResponse(payment);
    }

    /**
     * 내 결제 이력 조회
     */
    @Override
    public List<PaymentResponse> findPaymentsByUser(Long userSq) {
        List<Payment> payments = paymentRepository.findAllByUserSqOrderByCreatedDtDesc(userSq);

        return payments.stream()
                .map(p -> paymentMapper.entityToResponse(p))
                .toList();
    }

    /**
     * 결제 취소 (환불)
     */
    @Override
    @Transactional
    public PaymentResponse cancelPayment(Long userSq, Long paymentSq, String reason) {
        // [수정] 조회 실패 -> 404 Not Found
        Payment payment = paymentRepository.findById(paymentSq)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 결제 내역입니다."));

        // [수정] 권한 없음 -> 403 Forbidden
        if (!payment.getUserSq().equals(userSq)) throw new SecurityException("본인의 결제만 취소 가능");

        // 상태 충돌 -> 409 Conflict 유지
        if (payment.getStatus() != PaymentStatus.DONE) throw new IllegalStateException("완료된 결제만 취소 가능");

        if (payment.getPointAmount() > 0) {
            accountClient.increasePoint(userSq, new PointUseRequest(payment.getPointAmount()));
        }
        if (payment.getPgAmount() > 0) {
            pgClient.cancelPayment(payment.getPgKey(), reason, null);
        }

        if (payment.getRefType() == PaymentRefType.BOOKING) {
            Booking booking = bookingService.findByOrderId(payment.getOrderId());
            // 로직상 연결된 예매가 없으면 데이터 무결성 문제 -> 409 Conflict 또는 500이 적절 (IllegalStateException 유지)
            if (booking == null) throw new IllegalStateException("연결된 예매 정보를 찾을 수 없습니다.");

            List<Long> seatIds = getSeatIdsFromBooking(booking);
            performanceClient.cancelRoundSeats(seatIds);
            booking.cancel(reason);
        }

        payment.cancel();
        return paymentMapper.entityToResponse(payment);
    }

    /**
     * Helper: 결제 수단 결정
     */
    private PaymentMethod determinePaymentMethod(long pgAmount, long pointAmount) {
        if (pgAmount > 0 && pointAmount > 0) return PaymentMethod.MIX;
        if (pgAmount > 0) return PaymentMethod.CARD;
        if (pointAmount > 0) return PaymentMethod.POINT;
        throw new IllegalStateException("0원 결제 불가");
    }

    private List<Long> getSeatIdsFromBooking(Booking booking) {
        if (booking.getTickets() == null || booking.getTickets().isEmpty()) return Collections.emptyList();
        return booking.getTickets().stream().map(ticket -> ticket.getRoundSeatSq()).toList();
    }
}