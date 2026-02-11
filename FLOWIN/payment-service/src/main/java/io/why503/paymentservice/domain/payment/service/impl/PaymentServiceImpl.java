package io.why503.paymentservice.domain.payment.service.impl;

import io.why503.paymentservice.domain.payment.mapper.PaymentMapper;
import io.why503.paymentservice.domain.payment.model.dto.request.PaymentCancelRequest;
import io.why503.paymentservice.domain.payment.model.dto.request.PaymentRequest;
import io.why503.paymentservice.domain.payment.model.dto.response.PaymentResponse;
import io.why503.paymentservice.domain.payment.model.entity.Payment;
import io.why503.paymentservice.domain.payment.model.enums.PaymentStatus;
import io.why503.paymentservice.domain.payment.repository.PaymentRepository;
import io.why503.paymentservice.domain.payment.service.PaymentService;
import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory;
import io.why503.paymentservice.domain.point.model.entity.Point;
import io.why503.paymentservice.domain.point.model.enums.PointStatus;
import io.why503.paymentservice.domain.point.service.PointService;
import io.why503.paymentservice.domain.ticket.model.dto.response.TicketResponse;
import io.why503.paymentservice.domain.ticket.service.TicketService;
import io.why503.paymentservice.global.client.AccountClient;
import io.why503.paymentservice.global.client.PerformanceClient;
import io.why503.paymentservice.global.client.PgClient;
import io.why503.paymentservice.global.client.ReservationClient;
import io.why503.paymentservice.global.client.dto.request.PointUseRequest;
import io.why503.paymentservice.global.client.dto.response.BookingResponse;
import io.why503.paymentservice.global.client.dto.response.BookingSeatResponse;
import io.why503.paymentservice.global.client.dto.response.RoundSeatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        List<Long> seatIds = booking.bookingSeats().stream()
                .map(seat -> seat.roundSeatSq())
                .toList();

        List<RoundSeatResponse> seatDetails = performanceClient.findRoundSeats(seatIds);
        Map<Long, RoundSeatResponse> seatMap = seatDetails.stream()
                .collect(Collectors.toMap(
                        data -> data.roundSeatSq(),
                        data -> data
                ));

        long serverTotalAmount = 0;

        for (BookingSeatResponse seat : booking.bookingSeats()) {
            RoundSeatResponse seatInfo = seatMap.get(seat.roundSeatSq());
            if (seatInfo == null) {
                throw PaymentExceptionFactory.paymentNotFound("좌석 정보를 찾을 수 없습니다: " + seat.roundSeatSq());
            }

            long originalPrice = seatInfo.price();
            long discountPercent = seat.discountPolicy().getDiscountPercent();
            long discountAmount = (originalPrice * discountPercent) / 100;

            serverTotalAmount += (originalPrice - discountAmount);
        }

        // 요청 금액과 서버 계산 금액이 다르면 예외 발생 (변조 방지)
        if (request.totalAmount() != serverTotalAmount) {
            log.warn("결제 금액 불일치. 요청: {}, 서버계산: {}", request.totalAmount(), serverTotalAmount);
            throw PaymentExceptionFactory.paymentBadRequest("요청된 결제 총액이 올바르지 않습니다.");
        }

        long usePoint = request.usePointAmount();
        if (usePoint > serverTotalAmount) {
            throw PaymentExceptionFactory.paymentBadRequest("결제 금액보다 많은 포인트를 사용할 수 없습니다.");
        }
        if (usePoint < 0) {
            throw PaymentExceptionFactory.paymentBadRequest("포인트 사용액은 0보다 작을 수 없습니다.");
        }

        long finalPgAmount = serverTotalAmount - usePoint;

        if (usePoint > 0) {
            try {
                // AccountService가 존재한다고 가정
                accountClient.decreasePoint(userSq, new PointUseRequest(usePoint));
            } catch (Exception e) {
                throw PaymentExceptionFactory.paymentConflict("포인트 차감 실패: 잔액 부족 또는 시스템 오류");
            }
        }

        String approvedPgKey = null;

        if (finalPgAmount > 0) {
            try {
                approvedPgKey = pgClient.approvePayment(request.paymentKey(), request.orderId(), finalPgAmount);
            } catch (Exception e) {
                // PG 실패 시 포인트 롤백
                if (usePoint > 0) {
                    accountClient.increasePoint(userSq, new PointUseRequest(usePoint));
                }
                throw PaymentExceptionFactory.paymentConflict("PG 결제 승인 실패");
            }
        } else {
            // 전액 포인트 결제인 경우
            approvedPgKey = "POINT_FULL_PAYMENT";
        }

        try {
            performanceClient.confirmRoundSeats(userSq, seatIds);
            reservationClient.confirmPaid(userSq, booking.sq());

        } catch (Exception e) {
            log.error("결제 후처리 실패 - 롤백 수행: {}", e.getMessage());

            // 보상 트랜잭션 수행
            if (finalPgAmount > 0 && !"POINT_FULL_PAYMENT".equals(approvedPgKey)) {
                pgClient.cancelPayment(approvedPgKey, "시스템 오류: 좌석 확정 실패", finalPgAmount);
            }
            if (usePoint > 0) {
                accountClient.increasePoint(userSq, new PointUseRequest(usePoint));
            }

            throw PaymentExceptionFactory.paymentConflict("좌석 확정 처리에 실패하여 결제가 취소되었습니다.");
        }

        Payment payment = paymentMapper.responseToBookingEntity(userSq, booking.sq(), request);
        payment.complete(approvedPgKey);
        payment = paymentRepository.save(payment);

        ticketService.issueTickets(userSq, payment, booking.sq(), booking.bookingSeats());

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
                .map(payment -> paymentMapper.entityToResponse(payment))
                .toList();
    }

    @Override
    @Transactional
    public PaymentResponse cancelPayment(Long userSq, Long paymentSq, PaymentCancelRequest request) {
        Payment payment = paymentRepository.findById(paymentSq)
                .orElseThrow(() -> PaymentExceptionFactory.paymentNotFound("존재하지 않는 결제 내역입니다."));

        if (!payment.getUserSq().equals(userSq)) {
            throw PaymentExceptionFactory.paymentForbidden("본인의 결제만 취소 가능합니다.");
        }

        if (payment.getStatus() == PaymentStatus.CANCELED) {
            throw PaymentExceptionFactory.paymentConflict("이미 전액 취소된 결제입니다.");
        }

        List<Long> cancelSeatIds = request.seatIds();
        long refundAmount = 0;

        BookingResponse booking = null;
        try {
            booking = reservationClient.getBooking(userSq, payment.getBookingSq());
        } catch (Exception e) {
            log.warn("취소 중 예매 정보 조회 실패: {}", e.getMessage());
            if (cancelSeatIds != null && !cancelSeatIds.isEmpty()) {
                throw PaymentExceptionFactory.paymentNotFound("예매 정보를 확인할 수 없어 부분 취소를 진행할 수 없습니다.");
            }
        }

        if (cancelSeatIds == null || cancelSeatIds.isEmpty()) {
            refundAmount = payment.getRemainPgAmount() + payment.getRemainPointAmount();

            if (booking != null) {
                cancelSeatIds = booking.bookingSeats().stream()
                        .map(bookingSeatResponse -> bookingSeatResponse.roundSeatSq())
                        .toList();
            }
        } else {
            List<TicketResponse> ticketsToCancel = ticketService.findTicketsByRoundSeats(cancelSeatIds);
            for (TicketResponse ticket : ticketsToCancel) {
                refundAmount += ticket.finalPrice();
            }
        }

        long refundPg = 0;
        long refundPoint = 0;

        long remainPg = payment.getRemainPgAmount();
        long remainPoint = payment.getRemainPointAmount();

        if (refundAmount > (remainPg + remainPoint)) {
            throw PaymentExceptionFactory.paymentBadRequest("환불 요청 금액이 남은 결제 잔액을 초과합니다.");
        }

        if (remainPg >= refundAmount) {
            refundPg = refundAmount;
        } else {
            refundPg = remainPg;
            refundPoint = refundAmount - remainPg;
        }

        if (refundPoint > 0) {
            accountClient.increasePoint(userSq, new PointUseRequest(refundPoint));
        }

        if (refundPg > 0) {
            String cancelReason = "사용자 요청 취소";
            if (request.reason() != null) {
                cancelReason = request.reason();
            }
            pgClient.cancelPayment(payment.getPaymentKey(), cancelReason, refundPg);
        }

        if (booking != null && cancelSeatIds != null && !cancelSeatIds.isEmpty()) {
            performanceClient.cancelRoundSeats(cancelSeatIds);
            reservationClient.refundSeats(userSq, payment.getBookingSq(), cancelSeatIds);
            ticketService.resetTickets(cancelSeatIds);
        }

        payment.cancel(refundPg, refundPoint);

        return paymentMapper.entityToResponse(payment);
    }
}