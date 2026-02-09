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
 * 결제 승인 및 환불과 관련된 핵심 비즈니스 로직을 수행하는 서비스
 * - 외부 PG사 및 타 마이크로서비스와의 연동을 통해 거래의 최종 상태를 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PointService pointService;
    private final TicketService ticketService;

    private final AccountClient accountClient;
    private final PgClient pgClient;
    private final PerformanceClient performanceClient;
    private final ReservationClient reservationClient;

    // 결제 요청의 유효성을 검증하고 주문 유형에 따른 승인 프로세스 진행
    @Override
    @Transactional
    public PaymentResponse pay(Long userSq, PaymentRequest request) {
        if (paymentRepository.findByOrderId(request.orderId()).isPresent()) {
            throw PaymentExceptionFactory.paymentConflict("이미 처리된 주문 번호입니다.");
        }

        Payment payment;
        String orderId = request.orderId();

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

    // 예매 데이터 확인, 포인트 차감, PG 승인 및 좌석 확정 처리
    private Payment processBookingPayment(Long userSq, PaymentRequest request) {
        BookingResponse booking;
        try {
            booking = reservationClient.findBookingByOrderId(request.orderId());
        } catch (Exception e) {
            log.error("예매 정보 조회 실패. OrderId: {}, Error: {}", request.orderId(), e.getMessage());
            throw PaymentExceptionFactory.paymentNotFound("예매 정보를 찾을 수 없습니다.");
        }

        if (booking == null) {
            throw PaymentExceptionFactory.paymentNotFound("유효하지 않은 예매 주문 번호입니다.");
        }

        if (!booking.userSq().equals(userSq)) {
            throw PaymentExceptionFactory.paymentForbidden("본인의 예매 건만 결제할 수 있습니다.");
        }

        if (!"PENDING".equals(booking.status())) {
            throw PaymentExceptionFactory.paymentConflict("결제 가능한 상태가 아닙니다.");
        }

        long usePoint = request.usePointAmount();
        long pgAmount = request.totalAmount() - usePoint;

        if (usePoint > 0) {
            accountClient.decreasePoint(userSq, new PointUseRequest(usePoint));
        }

        String approvedPgKey = null;
        if (pgAmount > 0) {
            try {
                approvedPgKey = pgClient.approvePayment(request.paymentKey(), request.orderId(), pgAmount);
            } catch (Exception e) {
                if (usePoint > 0) accountClient.increasePoint(userSq, new PointUseRequest(usePoint));
                throw PaymentExceptionFactory.paymentConflict("PG 결제 승인 실패");
            }
        }

        try {
            List<Long> seatIds = booking.roundSeatSqs();
            performanceClient.confirmRoundSeats(userSq, seatIds);
            reservationClient.confirmPaid(userSq, booking.sq());

        } catch (Exception e) {
            log.error("결제 후처리 실패 - 롤백 수행: {}", e.getMessage());
            if (approvedPgKey != null) pgClient.cancelPayment(approvedPgKey, "시스템 오류: 좌석 확정 실패", pgAmount);
            if (usePoint > 0) accountClient.increasePoint(userSq, new PointUseRequest(usePoint));

            throw PaymentExceptionFactory.paymentConflict("좌석 확정 처리에 실패하여 결제가 취소되었습니다.");
        }

        Payment payment = paymentMapper.responseToBookingEntity(userSq, booking.sq(), request);
        payment.complete(approvedPgKey);
        payment = paymentRepository.save(payment);

        ticketService.issueTickets(userSq, payment, booking.sq(), booking.roundSeatSqs());

        return payment;
    }

    // 카드 결제 승인을 통한 사용자 포인트 충전 및 상태 업데이트
    private Payment processPointChargePayment(Long userSq, Point point, PaymentRequest request) {
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

        String approvedPgKey = pgClient.approvePayment(request.paymentKey(), request.orderId(), request.totalAmount());
        accountClient.increasePoint(userSq, new PointUseRequest(point.getChargeAmount()));

        Payment payment = paymentMapper.responseToPointEntity(userSq, request.orderId(), request.totalAmount());
        payment.complete(approvedPgKey);
        point.complete();

        return paymentRepository.save(payment);
    }

    @Override
    public PaymentResponse findPayment(Long userSq, Long paymentSq) {
        Payment payment = paymentRepository.findById(paymentSq)
                .orElseThrow(() -> PaymentExceptionFactory.paymentNotFound("존재하지 않는 결제 내역입니다."));

        if (!payment.getUserSq().equals(userSq)) {
            throw PaymentExceptionFactory.paymentForbidden("본인의 결제 내역만 조회할 수 있습니다.");
        }

        return paymentMapper.entityToResponse(payment);
    }

    @Override
    public List<PaymentResponse> findPaymentsByUser(Long userSq) {
        return paymentRepository.findAllByUserSqOrderByCreatedDtDesc(userSq).stream()
                .map(paymentMapper::entityToResponse)
                .toList();
    }

    // 사용된 자산의 회수 및 타 서비스의 예매/좌석 정보 무효화 처리
    @Override
    @Transactional
    public PaymentResponse cancelPayment(Long userSq, Long paymentSq, String reason) {
        Payment payment = paymentRepository.findById(paymentSq)
                .orElseThrow(() -> PaymentExceptionFactory.paymentNotFound("존재하지 않는 결제 내역입니다."));

        if (!payment.getUserSq().equals(userSq)) throw PaymentExceptionFactory.paymentForbidden("본인의 결제만 취소 가능합니다.");
        if (payment.getStatus() != PaymentStatus.DONE) throw PaymentExceptionFactory.paymentConflict("완료된 결제만 취소 가능합니다.");

        if (payment.getPointAmount() > 0) {
            accountClient.increasePoint(userSq, new PointUseRequest(payment.getPointAmount()));
        }
        if (payment.getPgAmount() > 0) {
            pgClient.cancelPayment(payment.getPaymentKey(), reason, null);
        }

        if (payment.getRefType() == PaymentRefType.BOOKING) {
            Long bookingSq = payment.getBookingSq();
            BookingResponse booking = null;
            try {
                booking = reservationClient.getBooking(userSq, bookingSq);
            } catch (Exception e) {
                log.warn("취소 중 예매 정보 조회 실패: {}", e.getMessage());
            }

            if (booking != null) {
                List<Long> seatIds = booking.roundSeatSqs();
                performanceClient.cancelRoundSeats(seatIds);
                reservationClient.refundSeats(userSq, bookingSq, seatIds);
                ticketService.resetTickets(seatIds);
            }
        }

        payment.cancel();

        return paymentMapper.entityToResponse(payment);
    }
}