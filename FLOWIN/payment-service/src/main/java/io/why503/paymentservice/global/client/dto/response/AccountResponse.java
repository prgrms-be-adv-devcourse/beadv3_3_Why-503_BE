package io.why503.paymentservice.global.client.dto.response;

/**
 * 외부 서비스로부터 수신한 사용자별 포인트 잔액 정보
 */
public record AccountResponse(
        Long userPoint
) {
}