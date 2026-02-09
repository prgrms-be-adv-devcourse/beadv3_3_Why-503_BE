package io.why503.paymentservice.global.client.dto.request;

/**
 * 외부 서비스와의 포인트 잔액 변동 동기화를 위한 금액 정보 객체
 * - 포인트의 충전, 사용, 환불 시 필요한 가치 정보를 전달
 */
public record PointUseRequest(
        Long amount
) {
}