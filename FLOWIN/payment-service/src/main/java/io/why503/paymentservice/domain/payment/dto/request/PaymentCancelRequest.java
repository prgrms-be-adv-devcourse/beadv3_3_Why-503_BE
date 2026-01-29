package io.why503.paymentservice.domain.payment.dto.request;

public record PaymentCancelRequest(
        String orderId,
        Long ticketSq,
        String cancelReason,
        Integer cancelAmount
) {
}