package io.why503.paymentservice.global.client.dto;

public record PointUseRequest(
        Long amount // 사용할(차감할) 포인트 금액
) {
}