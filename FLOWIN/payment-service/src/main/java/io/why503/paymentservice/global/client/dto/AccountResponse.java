package io.why503.paymentservice.global.client.dto;

public record AccountResponse(
        String name,
        Integer point // 보유 포인트
) {
}