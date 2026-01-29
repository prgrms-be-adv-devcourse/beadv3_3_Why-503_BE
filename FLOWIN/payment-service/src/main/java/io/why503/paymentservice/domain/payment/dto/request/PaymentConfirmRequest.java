package io.why503.paymentservice.domain.payment.dto.request;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        Integer amount
) {
}