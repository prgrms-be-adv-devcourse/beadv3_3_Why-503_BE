package io.why503.paymentservice.domain.payment.dto;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        Integer amount
) {
}