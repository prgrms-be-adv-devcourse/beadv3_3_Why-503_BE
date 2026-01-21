package io.why503.paymentservice.global.client.dto;

public record PointUseRequest(
        Long userSq,   // 사용자 식별자
        Integer amount // 사용할(차감할) 포인트 금액
) {
}