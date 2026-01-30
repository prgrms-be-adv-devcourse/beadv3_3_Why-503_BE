package io.why503.paymentservice.global.client.dto;

public record AccountResponse(
        String userName,
        Long userPoint // 보유 포인트
) {
}