package io.why503.paymentservice.domain.payment.dto;

public record PointChargeResponse(
        String orderId,
        Long amount,
        String orderName
) {}