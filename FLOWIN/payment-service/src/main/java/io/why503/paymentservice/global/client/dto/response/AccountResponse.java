package io.why503.paymentservice.global.client.dto.response;

/**
 * 외부 계정 서비스로부터 수신한 사용자 이름과 포인트 잔액 정보를 담는 객체
 */
public record AccountResponse(
        Long userPoint
) {
}