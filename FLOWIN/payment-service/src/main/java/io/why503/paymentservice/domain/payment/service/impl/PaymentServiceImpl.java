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
import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory;
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
import java.util.NoSuchElementException;

/**
 * 예매 및 포인트 충전 건에 대한 결제 승인 요청과 상태 변경을 처리하는 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    private final BookingService bookingService;
    private final PointService pointService;

    private final AccountClient accountClient;
    private final PgClient pgClient;
    private final PerformanceClient performanceClient;

    // 결제 요청 대상에 따른 승인 프로세스 분기 처리
    @Override
    @Transactional
    public PaymentResponse pay(Long userSq, PaymentRequest request) {
        if (paymentRepository.findByOrderId(request.orderId()).isPresent()) {
            throw PaymentExceptionFactory.paymentConflict("이미 처리된 주문 번호입니다.");
        }

        Payment payment;
        String orderId = request.orderId();

        if (orderId.startsWith("BOOKING-")) {
            Booking booking = bookingService.findByOrderId(orderId);
            if (booking == null) {
                throw PaymentExceptionFactory.paymentNotFound("유효하지 않은 예매 주문 번호입니다.");
            }
            payment = processBookingPayment(userSq, booking, request);

        } else if (orderId.startsWith("POINT-")) {
            Point point = pointService.findByOrderId(orderId);
            if (point == null) {
                throw PaymentExceptionFactory.paymentNotFound("유효하지 않은 포인트 충전 주문 번호입니다.");
            }
            payment = processPointChargePayment(userSq, point, request);

        } else {
            throw PaymentExceptionFactory.paymentBadRequest("지원하지 않는 주문 번호 형식입니다.");
        }

        return paymentMapper.entityToResponse(payment);
    }

    // 예매 데이터에 대한 결제 승인 및 실제 좌석 예약 확정
    private Payment processBookingPayment(Long userSq, Booking booking, PaymentRequest request) {
        /*
         * 1. 예매 내역 소유권 및 결제 금액 정합성 검증
         * 2. 포인트 차감 및 외부 연동을 통한 결제 승인
         * 3. 좌석 예약 상태를 확정으로 변경
         * 4. 결제 내역 저장 및 예매 정보 업데이트
         */
        if (!booking.getUserSq().equals(userSq)) {
            throw PaymentExceptionFactory.paymentForbidden("본인 예매만 결제 가능");
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw PaymentExceptionFactory.paymentConflict("결제 가능 상태 아님");
        }

        if (request.amount().longValue() != booking.getFinalAmount().longValue()) {
            throw PaymentExceptionFactory.paymentBadRequest("결제 요청 금액과 예매 금액이 불일치합니다.");
        }

        long usePoint = request.usePointAmount();
        long pgAmount = request.amount() - usePoint;
        PaymentMethod method = determinePaymentMethod(pgAmount, usePoint);

        if (usePoint > 0) {
            accountClient.decreasePoint(userSq, new PointUseRequest(usePoint));
        }

        String approvedPgKey = null;
        if (pgAmount > 0) {
            try {
                approvedPgKey = pgClient.approvePayment(request.paymentKey(), request.orderId(), pgAmount);
            } catch (Exception e) {
                if (usePoint > 0) accountClient.increasePoint(userSq, new PointUseRequest(usePoint));
                throw PaymentExceptionFactory.paymentConflict("PG 결제 승인 실패: " + e.getMessage());
            }
        }

        try {
            List<Long> seatIds = getSeatIdsFromBooking(booking);
            performanceClient.confirmRoundSeats(userSq, seatIds);
        } catch (Exception e) {
            if (approvedPgKey != null) pgClient.cancelPayment(approvedPgKey, "시스템 오류: 좌석 확정 실패", pgAmount);
            if (usePoint > 0) accountClient.increasePoint(userSq, new PointUseRequest(usePoint));
            throw PaymentExceptionFactory.paymentConflict("좌석 확정 실패. 결제 취소됨.");
        }

        Payment payment = paymentMapper.responseToBookingEntity(
                userSq, request.orderId(), method, request.amount(), pgAmount, usePoint
        );

        payment.approve(approvedPgKey);
        booking.confirm();
        return paymentRepository.save(payment);
    }

    // 포인트 충전 요청에 대한 승인 처리 및 계액 서비스 반영
    private Payment processPointChargePayment(Long userSq, Point point, PaymentRequest request) {
        /*
         * 1. 충전 요청 유효성 및 금액 일치 여부 확인
         * 2. 외부 결제 승인 및 실제 포인트 잔액 증가
         * 3. 결제 내역 생성 및 충전 요청 상태 완료 처리
         */
        if (!point.getUserSq().equals(userSq)) {
            throw PaymentExceptionFactory.paymentForbidden("본인의 충전 요청만 결제할 수 있습니다.");
        }
        if (point.getStatus() != PointStatus.READY) {
            throw PaymentExceptionFactory.paymentConflict("충전 가능한 상태가 아닙니다.");
        }

        if (request.usePointAmount() > 0) {
            throw PaymentExceptionFactory.paymentBadRequest("포인트 충전 시 포인트를 사용할 수 없습니다.");
        }
        if (!request.amount().equals(point.getChargeAmount())) {
            throw PaymentExceptionFactory.paymentBadRequest("요청 금액이 충전 신청 금액과 다릅니다.");
        }

        String approvedPgKey = pgClient.approvePayment(request.paymentKey(), request.orderId(), request.amount());

        accountClient.increasePoint(userSq, new PointUseRequest(point.getChargeAmount()));

        Payment payment = paymentMapper.responseToPointEntity(userSq, request.orderId(), request.amount());

        payment.approve(approvedPgKey);
        point.complete();

        return paymentRepository.save(payment);
    }

    // 결제 내역 상세 정보 조회
    @Override
    public PaymentResponse findPayment(Long userSq, Long paymentSq) {
        Payment payment = paymentRepository.findById(paymentSq)
                .orElseThrow(() -> PaymentExceptionFactory.paymentNotFound("존재하지 않는 결제 내역입니다."));

        if (!payment.getUserSq().equals(userSq)) {
            throw PaymentExceptionFactory.paymentForbidden("본인의 결제 내역만 조회할 수 있습니다.");
        }

        return paymentMapper.entityToResponse(payment);
    }

    // 사용자의 전체 결제 이력 목록 조회
    @Override
    public List<PaymentResponse> findPaymentsByUser(Long userSq) {
        List<Payment> payments = paymentRepository.findAllByUserSqOrderByCreatedDtDesc(userSq);

        return payments.stream()
                .map(p -> paymentMapper.entityToResponse(p))
                .toList();
    }

    // 승인 완료된 결제 건의 취소 및 환불 프로세스 수행
    @Override
    @Transactional
    public PaymentResponse cancelPayment(Long userSq, Long paymentSq, String reason) {
        /*
         * 1. 취소 가능 여부 및 소유권 검증
         * 2. 결제 수단별 포인트 환불 및 PG 취소 연동
         * 3. 원천 대상이 예매인 경우 좌석 및 예매 데이터 원복
         * 4. 결제 상태 취소로 변경
         */
        Payment payment = paymentRepository.findById(paymentSq)
                .orElseThrow(() -> PaymentExceptionFactory.paymentNotFound("존재하지 않는 결제 내역입니다."));

        if (!payment.getUserSq().equals(userSq)) throw PaymentExceptionFactory.paymentForbidden("본인의 결제만 취소 가능");

        if (payment.getStatus() != PaymentStatus.DONE) throw PaymentExceptionFactory.paymentConflict("완료된 결제만 취소 가능");

        if (payment.getPointAmount() > 0) {
            accountClient.increasePoint(userSq, new PointUseRequest(payment.getPointAmount()));
        }
        if (payment.getPgAmount() > 0) {
            pgClient.cancelPayment(payment.getPgKey(), reason, null);
        }

        if (payment.getRefType() == PaymentRefType.BOOKING) {
            Booking booking = bookingService.findByOrderId(payment.getOrderId());
            if (booking == null) throw PaymentExceptionFactory.paymentConflict("연결된 예매 정보를 찾을 수 없습니다.");

            List<Long> seatIds = getSeatIdsFromBooking(booking);
            performanceClient.cancelRoundSeats(seatIds);
            booking.cancel(reason);
        }

        payment.cancel();
        return paymentMapper.entityToResponse(payment);
    }

    // 결제 금액 구성에 따른 결제 수단 분류
    private PaymentMethod determinePaymentMethod(long pgAmount, long pointAmount) {
        if (pgAmount > 0 && pointAmount > 0) return PaymentMethod.MIX;
        if (pgAmount > 0) return PaymentMethod.CARD;
        if (pointAmount > 0) return PaymentMethod.POINT;
        throw PaymentExceptionFactory.paymentBadRequest("0원 결제 불가");
    }

    // 예매 엔티티에서 좌석 식별자 목록 추출
    private List<Long> getSeatIdsFromBooking(Booking booking) {
        if (booking.getTickets() == null || booking.getTickets().isEmpty()) return Collections.emptyList();
        return booking.getTickets().stream().map(ticket -> ticket.getRoundSeatSq()).toList();
    }
}