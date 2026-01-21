package io.why503.paymentservice.domain.payment.service;

import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import io.why503.paymentservice.domain.booking.model.vo.BookingStatus;
import io.why503.paymentservice.domain.booking.repository.BookingRepository;
import io.why503.paymentservice.domain.booking.service.BookingService;
import io.why503.paymentservice.domain.payment.config.TossPaymentConfig;
import io.why503.paymentservice.domain.payment.dto.PaymentCancelRequest;
import io.why503.paymentservice.domain.payment.dto.PaymentConfirmRequest;
import io.why503.paymentservice.domain.payment.dto.TossPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 결제 서비스
 * - 담당: PG사(토스) 승인/취소 요청, BookingService 상태 변경 호출
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final TossPaymentConfig tossPaymentConfig;
    private final RestTemplate restTemplate;

    private static final String TOSS_API_URL = "https://api.tosspayments.com/v1/payments/";

    /**
     * 결제 승인
     * 1. 주문 유효성 및 금액 검증
     * 2. PG사 승인 API 호출
     * 3. BookingService 확정 처리
     */
    @Transactional
    public void confirmPayment(PaymentConfirmRequest request) {

        // 1. booking 조회
        Booking booking = bookingRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 2. 상태 검증
        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 결제입니다.");
        }

        // 3. 금액 검증
        if (!booking.getPgAmount().equals(request.getAmount())) {
            throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
        }

        // 4. 결제 승인 요청
        try {
            TossPaymentResponse result = requestConfirmToToss(request.getPaymentKey(), request.getOrderId(), request.getAmount());

            if (result != null) {
                // 엔티티에 영수증 URL 설정
                booking.setReceiptUrl(result.getReceipt().getUrl());

                // BookingService 확정 호출
                bookingService.confirmBooking(
                        booking.getBookingSq(),
                        result.getPaymentKey(),
                        result.getMethod()
                );
            }

        } catch (HttpClientErrorException e) {
            // 5. 승인 실패 처리
            booking.cancel("결제 승인 실패: " + e.getResponseBodyAsString());
            throw e;
        }
    }

    /**
     * 결제 취소 (환불)
     * - ticketSq 유무에 따라 전체/부분 취소를 분기합니다.
     */
    @Transactional
    public void cancelPayment(PaymentCancelRequest request) {
        // 1. Booking 조회
        Booking booking = bookingRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        String paymentKey = booking.getPaymentKey();
        if (paymentKey == null) {
            throw new IllegalStateException("결제 내역이 존재하지 않아 취소할 수 없습니다.");
        }

        Integer cancelAmount = null; // null이면 전액 취소

        // 2. 취소 로직 분기 (전체 vs 부분)
        if (request.getTicketSq() != null) {
            // [부분 취소]
            // 해당 티켓 가격 확인
            Ticket targetTicket = booking.getTickets().stream()
                    .filter(t -> t.getTicketSq().equals(request.getTicketSq()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("해당 예매에 존재하지 않는 티켓입니다."));

            cancelAmount = targetTicket.getFinalPrice();

            // DB 및 내부 상태 취소
            bookingService.cancelTicket(booking.getBookingSq(), request.getTicketSq());

        } else {
            // [전체 취소]
            bookingService.cancelBooking(booking.getBookingSq());
        }

        // 3. PG사 결제 취소 요청
        try {
            requestCancelToToss(paymentKey, request.getCancelReason(), cancelAmount);
        } catch (Exception e) {
            log.error("PG사 취소 요청 실패: {}", e.getMessage());
            throw new IllegalStateException("PG사 결제 취소에 실패했습니다. (DB 롤백됨)");
        }
    }

    /**
     * 결제 실패 처리
     */
    @Transactional
    public void failPayment(String orderId, String reason) {
        Booking booking = bookingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (booking.getBookingStatus() == BookingStatus.PENDING) {
            booking.cancel(reason != null ? reason : "결제 실패");
        }
    }

    // --- Private Helper Methods (Toss API) ---

    /**
     * 토스 승인 API 호출
     */
    private TossPaymentResponse requestConfirmToToss(String paymentKey, String orderId, Integer amount) {
        HttpHeaders httpHeaders = getTossHeaders();

        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", paymentKey);
        body.put("orderId", orderId);
        body.put("amount", amount);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        String url = TOSS_API_URL + "confirm";

        return restTemplate.postForEntity(url, entity, TossPaymentResponse.class).getBody();
    }

    /**
     * 토스 취소 API 호출
     */
    private void requestCancelToToss(String paymentKey, String reason, Integer cancelAmount) {
        HttpHeaders httpHeaders = getTossHeaders();

        Map<String, Object> body = new HashMap<>();
        body.put("cancelReason", reason);
        // 부분 취소일 경우 금액 포함, 없으면 전체 취소
        if (cancelAmount != null) {
            body.put("cancelAmount", cancelAmount);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        String url = TOSS_API_URL + paymentKey + "/cancel";

        restTemplate.postForEntity(url, entity, Map.class);
    }

    /**
     * 토스 API 헤더 생성 (Authorization)
     */
    private HttpHeaders getTossHeaders() {
        String authValue = Base64.getEncoder().encodeToString(
                (tossPaymentConfig.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8)
        );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Basic " + authValue);
        httpHeaders.set("Content-Type", "application/json");
        return httpHeaders;
    }
}