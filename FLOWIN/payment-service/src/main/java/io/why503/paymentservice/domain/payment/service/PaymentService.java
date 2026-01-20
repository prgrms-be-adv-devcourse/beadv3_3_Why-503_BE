package io.why503.paymentservice.domain.payment.service;

import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.vo.BookingStatus;
import io.why503.paymentservice.domain.booking.repository.BookingRepository;
import io.why503.paymentservice.domain.payment.dto.PaymentConfirmRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate;

    private static final String TOSS_SECRET_KEY = "toss_test_key";
    private static final String CONFIRM_URL =
            "toss_test_url";

    public PaymentService(
            BookingRepository bookingRepository,
            RestTemplate restTemplate
    ) {
        this.bookingRepository = bookingRepository;
        this.restTemplate = restTemplate;
    }

    public void confirmPayment(PaymentConfirmRequest request) {

        /* 1. booking 조회 */
        Booking booking = bookingRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        /* 2. 상태 검증 */
        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 결제입니다.");
        }

        /* 3. 금액 검증 */
        if (booking.getPgAmount() != request.getAmount()) {
            throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
        }

        /* 4. Authorization 헤더 구성 */
        String authValue = Base64.getEncoder().encodeToString(
                (TOSS_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8)
        );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Basic " + authValue);
        httpHeaders.set("Content-Type", "application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", request.getPaymentKey());
        body.put("orderId", request.getOrderId());
        body.put("amount", request.getAmount());

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, httpHeaders);

        try {
            restTemplate.postForEntity(
                    CONFIRM_URL,
                    entity,
                    Void.class
            );

            /* 5. 승인 성공 */
            booking.confirm(
                    request.getPaymentKey(),
                    booking.getPaymentMethod()
            );

        } catch (HttpClientErrorException e) {

            /* 6. 승인 실패 */
            booking.cancel("결제 승인 실패");
            throw e;
        }
    }

    public void failPayment(String orderId, String reason) {

        Booking booking = bookingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            return;
        }

        booking.cancel(
                reason != null ? reason : "결제 실패"
        );
    }
}
