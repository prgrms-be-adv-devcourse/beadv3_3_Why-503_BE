package io.why503.paymentservice.domain.payment.service.impl;

import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import io.why503.paymentservice.domain.booking.model.enums.BookingStatus;
import io.why503.paymentservice.domain.booking.repository.BookingRepository;
import io.why503.paymentservice.domain.booking.service.BookingService;
import io.why503.paymentservice.domain.payment.config.TossPaymentConfig;
import io.why503.paymentservice.domain.payment.dto.request.PaymentCancelRequest;
import io.why503.paymentservice.domain.payment.dto.request.PaymentConfirmRequest;
import io.why503.paymentservice.domain.payment.dto.request.PointChargeRequest;
import io.why503.paymentservice.domain.payment.dto.response.PointChargeResponse;
import io.why503.paymentservice.domain.payment.dto.response.TossPaymentResponse;
import io.why503.paymentservice.domain.payment.service.PaymentService;
import io.why503.paymentservice.global.client.AccountClient;
import io.why503.paymentservice.global.client.dto.request.PointUseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 결제 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final TossPaymentConfig tossPaymentConfig;
    private final RestTemplate restTemplate;
    private final AccountClient accountClient;

    private static final String TOSS_API_URL = "https://api.tosspayments.com/v1/payments/";

    // 결제 승인 (최종)
    @Override
    @Transactional
    public void confirmPayment(PaymentConfirmRequest request, Long userSq) {
        Booking booking = validateBooking(request, userSq);

        try {
            // 1. PG사 승인 요청
            TossPaymentResponse result = requestConfirmToToss(request.paymentKey(), request.orderId(), request.amount());

            booking.setReceiptUrl(result.receipt().url());

            try {
                // 2. 내부 비즈니스 로직 수행
                if ("POINT_CHARGE".equals(booking.getPaymentMethod())) {
                    processPointCharge(booking, result.paymentKey(), userSq);
                } else {
                    // Refactor: bookingSq -> sq
                    bookingService.confirmBooking(
                            booking.getSq(),
                            result.paymentKey(),
                            result.method(),
                            userSq
                    );
                }
            } catch (Exception bizEx) {
                // 보상 트랜잭션 (Compensating Transaction)
                log.error(">>> [Payment] 내부 로직 실패로 인한 PG 자동 취소 진행: {}", bizEx.getMessage());

                try {
                    requestCancelToToss(result.paymentKey(), "시스템 오류(내부 로직 실패)로 인한 자동 취소", null);
                } catch (Exception cancelEx) {
                    log.error(">>> [CRITICAL] PG 취소 실패! 수동 환불 필요. Key={}", result.paymentKey());
                }
                throw bizEx;
            }

        } catch (Exception e) {
            log.error(">>> [Payment] 결제 프로세스 최종 실패: {}", e.getMessage());
            booking.cancel("결제 프로세스 실패: " + e.getMessage());
            throw e;
        }
    }

    // 결제 취소
    @Override
    @Transactional
    public void cancelPayment(PaymentCancelRequest request, Long userSq) {
        Booking booking = bookingRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        validateOwner(booking, userSq);

        if (booking.getPaymentKey() == null) {
            throw new IllegalStateException("결제 완료된 건만 취소할 수 있습니다.");
        }

        Integer cancelAmount = null;

        // 1. DB 및 내부 상태 취소
        if (request.ticketSq() != null) {
            // 부분 취소
            Ticket targetTicket = booking.getTickets().stream()
                    .filter(t -> t.getSq().equals(request.ticketSq())) // Refactor: ticketSq -> sq
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("티켓이 존재하지 않습니다."));

            cancelAmount = targetTicket.getFinalPrice();
            // Refactor: bookingSq -> sq
            bookingService.cancelTicket(booking.getSq(), request.ticketSq(), userSq);

        } else {
            // 전체 취소
            bookingService.cancelBooking(booking.getSq(), userSq);
        }

        // 2. PG사 취소 요청
        try {
            requestCancelToToss(booking.getPaymentKey(), request.cancelReason(), cancelAmount);
        } catch (Exception e) {
            log.error("PG사 취소 요청 실패: {}", e.getMessage());
            throw new IllegalStateException("PG사 결제 취소 실패 (잠시 후 다시 시도해주세요)");
        }
    }

    // 결제 실패 처리
    @Override
    @Transactional
    public void failPayment(String orderId, String reason, Long userSq) {
        Booking booking = bookingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        validateOwner(booking, userSq);

        // Refactor: bookingStatus -> status
        if (booking.getStatus() == BookingStatus.PENDING) {
            booking.cancel(reason != null ? reason : "결제 실패");
        }
    }

    // 포인트 충전 요청
    @Override
    @Transactional
    public PointChargeResponse requestPointCharge(Long userSq, PointChargeRequest request) {
        String orderId = "CHG-" + UUID.randomUUID();

        // Refactor: Builder 패턴 내 필드명 변경 반영
        Booking booking = Booking.builder()
                .userSq(userSq)
                .status(BookingStatus.PENDING)     // bookingStatus -> status
                .reservedAt(LocalDateTime.now())   // bookingDt -> reservedAt
                .originalAmount(0)                 // bookingAmount -> originalAmount
                .finalAmount(request.amount().intValue()) // totalAmount -> finalAmount
                .pgAmount(request.amount().intValue())
                .usedPoint(0)
                .paymentMethod("POINT_CHARGE")
                .orderId(orderId)
                .build();

        bookingRepository.save(booking);

        return new PointChargeResponse(orderId, request.amount(), "포인트 충전");
    }

    // --- Private Helper Methods ---

    private Booking validateBooking(PaymentConfirmRequest request, Long userSq) {
        Booking booking = bookingRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        validateOwner(booking, userSq);

        if (booking.getStatus() != BookingStatus.PENDING) { // Refactor
            throw new IllegalStateException("이미 처리된 결제입니다.");
        }
        if (!booking.getPgAmount().equals(request.amount())) {
            throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
        }
        return booking;
    }

    private void validateOwner(Booking booking, Long userSq) {
        if (!booking.getUserSq().equals(userSq)) {
            throw new IllegalArgumentException("본인의 주문만 처리할 수 있습니다.");
        }
    }

    private void processPointCharge(Booking booking, String paymentKey, Long userSq) {
        booking.setPaymentKey(paymentKey);
        booking.setStatus(BookingStatus.CONFIRMED); // Refactor
        booking.setApprovedAt(LocalDateTime.now());

        accountClient.increasePoint(userSq, new PointUseRequest(booking.getPgAmount().longValue()));
        log.info(">>> 포인트 충전 완료: User={}, Amount={}", userSq, booking.getPgAmount());
    }

    // Toss API Calls (RestTemplate)
    private TossPaymentResponse requestConfirmToToss(String paymentKey, String orderId, Integer amount) {
        HttpHeaders headers = getTossHeaders();
        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", paymentKey);
        body.put("orderId", orderId);
        body.put("amount", amount);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity(TOSS_API_URL + "confirm", entity, TossPaymentResponse.class).getBody();
    }

    private void requestCancelToToss(String paymentKey, String reason, Integer cancelAmount) {
        HttpHeaders headers = getTossHeaders();
        Map<String, Object> body = new HashMap<>();
        body.put("cancelReason", reason);
        if (cancelAmount != null) body.put("cancelAmount", cancelAmount);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(TOSS_API_URL + paymentKey + "/cancel", entity, Map.class);
    }

    private HttpHeaders getTossHeaders() {
        String authValue = Base64.getEncoder().encodeToString(
                (tossPaymentConfig.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8)
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + authValue);
        headers.set("Content-Type", "application/json");
        return headers;
    }
}