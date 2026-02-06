package io.why503.paymentservice.domain.payment.service.impl;

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
import io.why503.paymentservice.domain.ticket.service.TicketService;
import io.why503.paymentservice.global.client.AccountClient;
import io.why503.paymentservice.global.client.PerformanceClient;
import io.why503.paymentservice.global.client.PgClient;
import io.why503.paymentservice.global.client.ReservationClient;
import io.why503.paymentservice.global.client.dto.request.PointUseRequest;
import io.why503.paymentservice.global.client.dto.response.BookingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 결제 서비스 구현체
 * - MSA 구조 반영: Booking 엔티티 직접 참조 제거, Feign Client 사용
 * - TicketService를 통한 티켓 발권 및 초기화 위임
 * - OrderId 기반의 예매 정보 조회 및 검증 로직 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PointService pointService;
    private final TicketService ticketService; // [수정] Repository 대신 Service 사용

    // 외부 서비스 통신 Clients
    private final AccountClient accountClient;
    private final PgClient pgClient;
    private final PerformanceClient performanceClient;
    private final ReservationClient reservationClient;

    /**
     * 결제 승인 요청 처리
     */
    @Override
    @Transactional
    public PaymentResponse pay(Long userSq, PaymentRequest request) {
        // 1. 중복 결제 승인 방지
        if (paymentRepository.findByOrderId(request.orderId()).isPresent()) {
            throw PaymentExceptionFactory.paymentConflict("이미 처리된 주문 번호입니다.");
        }

        Payment payment;
        String orderId = request.orderId();

        // 2. 주문 유형에 따른 분기 처리 (예매 vs 포인트 충전)
        if (orderId.startsWith("BOOKING-")) {
            payment = processBookingPayment(userSq, request);

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

    /**
     * 예매 건에 대한 결제 프로세스
     */
    private Payment processBookingPayment(Long userSq, PaymentRequest request) {
        // 1. [외부 연동] ReservationService에서 주문 번호로 예매 정보 조회
        BookingResponse booking;
        try {
            booking = reservationClient.findBookingByOrderId(request.orderId());
        } catch (Exception e) {
            log.error("예매 정보 조회 실패. OrderId: {}, Error: {}", request.orderId(), e.getMessage());
            throw PaymentExceptionFactory.paymentNotFound("예매 정보를 찾을 수 없습니다. (서비스 통신 오류)");
        }

        if (booking == null) {
            throw PaymentExceptionFactory.paymentNotFound("유효하지 않은 예매 주문 번호입니다.");
        }

        // 2. 유효성 검증 (본인 확인, 상태 확인)
        if (!booking.userSq().equals(userSq)) {
            throw PaymentExceptionFactory.paymentForbidden("본인의 예매 건만 결제할 수 있습니다.");
        }

        // BookingResponse의 status가 String("PENDING")이라고 가정
        if (!"PENDING".equals(booking.status())) {
            throw PaymentExceptionFactory.paymentConflict("결제 가능한 상태가 아닙니다. (현재 상태: " + booking.status() + ")");
        }

        long usePoint = request.usePointAmount();
        long pgAmount = request.totalAmount() - usePoint;

        // 3. 포인트 차감 (Account Service)
        if (usePoint > 0) {
            accountClient.decreasePoint(userSq, new PointUseRequest(usePoint));
        }

        // 4. PG 승인 요청
        String approvedPgKey = null;
        if (pgAmount > 0) {
            try {
                approvedPgKey = pgClient.approvePayment(request.paymentKey(), request.orderId(), pgAmount);
            } catch (Exception e) {
                // 실패 시 포인트 롤백
                if (usePoint > 0) accountClient.increasePoint(userSq, new PointUseRequest(usePoint));
                throw PaymentExceptionFactory.paymentConflict("PG 결제 승인 실패: " + e.getMessage());
            }
        }

        // 5. 외부 서비스 상태 확정 (분산 트랜잭션 보상 처리 구간)
        try {
            List<Long> seatIds = booking.roundSeatSqs();

            // 5-1. 공연 서비스: 좌석 선점 확정
            performanceClient.confirmRoundSeats(userSq, seatIds);

            // 5-2. 예매 서비스: 결제 완료 상태 변경 (PENDING -> PAID)
            reservationClient.confirmPaid(userSq, booking.sq());

        } catch (Exception e) {
            log.error("결제 후처리 실패 - 롤백 수행: {}", e.getMessage());

            // 롤백: PG 취소 및 포인트 환불
            if (approvedPgKey != null) pgClient.cancelPayment(approvedPgKey, "시스템 오류: 좌석 확정 실패", pgAmount);
            if (usePoint > 0) accountClient.increasePoint(userSq, new PointUseRequest(usePoint));

            throw PaymentExceptionFactory.paymentConflict("좌석 확정 처리에 실패하여 결제가 취소되었습니다.");
        }

        // 6. 결제 내역 저장
        Payment payment = paymentMapper.responseToBookingEntity(
                userSq, booking.sq(), request
        );
        payment.complete(approvedPgKey);
        payment = paymentRepository.save(payment);

        // 7. 티켓 발권 (TicketService 위임)
        ticketService.issueTickets(userSq, payment, booking.sq(), booking.roundSeatSqs());

        return payment;
    }

    /**
     * 포인트 충전 건에 대한 결제 프로세스
     */
    private Payment processPointChargePayment(Long userSq, Point point, PaymentRequest request) {
        // 검증 로직
        if (!point.getUserSq().equals(userSq)) {
            throw PaymentExceptionFactory.paymentForbidden("본인의 충전 요청만 결제할 수 있습니다.");
        }
        if (point.getStatus() != PointStatus.READY) {
            throw PaymentExceptionFactory.paymentConflict("충전 가능한 상태가 아닙니다.");
        }
        if (request.usePointAmount() > 0) {
            throw PaymentExceptionFactory.paymentBadRequest("포인트 충전 시 포인트를 사용할 수 없습니다.");
        }
        if (!request.totalAmount().equals(point.getChargeAmount())) {
            throw PaymentExceptionFactory.paymentBadRequest("요청 금액이 충전 신청 금액과 다릅니다.");
        }

        // PG 승인
        String approvedPgKey = pgClient.approvePayment(request.paymentKey(), request.orderId(), request.totalAmount());

        // 포인트 증가 처리
        accountClient.increasePoint(userSq, new PointUseRequest(point.getChargeAmount()));

        // 결제 정보 저장
        Payment payment = paymentMapper.responseToPointEntity(userSq, request.orderId(), request.totalAmount());
        payment.complete(approvedPgKey);

        // 포인트 충전 완료 상태 변경
        point.complete();

        return paymentRepository.save(payment);
    }

    /**
     * 결제 상세 조회
     */
    @Override
    public PaymentResponse findPayment(Long userSq, Long paymentSq) {
        Payment payment = paymentRepository.findById(paymentSq)
                .orElseThrow(() -> PaymentExceptionFactory.paymentNotFound("존재하지 않는 결제 내역입니다."));

        if (!payment.getUserSq().equals(userSq)) {
            throw PaymentExceptionFactory.paymentForbidden("본인의 결제 내역만 조회할 수 있습니다.");
        }

        return paymentMapper.entityToResponse(payment);
    }

    /**
     * 사용자 결제 목록 조회
     */
    @Override
    public List<PaymentResponse> findPaymentsByUser(Long userSq) {
        return paymentRepository.findAllByUserSqOrderByCreatedDtDesc(userSq).stream()
                .map(paymentMapper::entityToResponse)
                .toList();
    }

    /**
     * 결제 취소 및 환불 처리
     */
    @Override
    @Transactional
    public PaymentResponse cancelPayment(Long userSq, Long paymentSq, String reason) {
        Payment payment = paymentRepository.findById(paymentSq)
                .orElseThrow(() -> PaymentExceptionFactory.paymentNotFound("존재하지 않는 결제 내역입니다."));

        if (!payment.getUserSq().equals(userSq)) throw PaymentExceptionFactory.paymentForbidden("본인의 결제만 취소 가능합니다.");
        if (payment.getStatus() != PaymentStatus.DONE) throw PaymentExceptionFactory.paymentConflict("완료된 결제만 취소 가능합니다.");

        // 1. 자산 환불 (포인트 반환, PG 취소)
        if (payment.getPointAmount() > 0) {
            accountClient.increasePoint(userSq, new PointUseRequest(payment.getPointAmount()));
        }
        if (payment.getPgAmount() > 0) {
            pgClient.cancelPayment(payment.getPaymentKey(), reason, null);
        }

        // 2. 예매 건인 경우 타 서비스 취소 요청
        if (payment.getRefType() == PaymentRefType.BOOKING) {
            Long bookingSq = payment.getBookingSq();

            // ReservationClient를 통해 예매 정보 조회 (취소할 좌석 정보 획득용)
            BookingResponse booking = null;
            try {
                booking = reservationClient.getBooking(userSq, bookingSq);
            } catch (Exception e) {
                log.warn("취소 중 예매 정보 조회 실패 (이미 삭제되었거나 통신 오류): {}", e.getMessage());
            }

            if (booking != null) {
                List<Long> seatIds = booking.roundSeatSqs();

                // 2-1. 공연 서비스: 좌석 선점 해제
                performanceClient.cancelRoundSeats(seatIds);

                // 2-2. 예매 서비스: 환불 처리 (예매 취소 + 좌석 해제 요청)
                reservationClient.refundSeats(userSq, bookingSq, seatIds);

                // 2-3. 티켓 서비스: 티켓 상태 초기화 (TicketService 위임)
                ticketService.resetTickets(seatIds);
            }
        }

        // 3. 결제 취소 상태로 변경
        payment.cancel();

        return paymentMapper.entityToResponse(payment);
    }
}