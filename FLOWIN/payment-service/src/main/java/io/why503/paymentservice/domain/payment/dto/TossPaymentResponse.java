package io.why503.paymentservice.domain.payment.dto;

public record TossPaymentResponse(
        String paymentKey,
        String orderId,
        String status,
        Long totalAmount,
        String method,
        String approvedAt,
        Receipt receipt
) {
    public record Receipt(String url) {
    }
}