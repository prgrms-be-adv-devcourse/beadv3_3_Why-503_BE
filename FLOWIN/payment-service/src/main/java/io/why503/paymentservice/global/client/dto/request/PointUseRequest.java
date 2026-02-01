package io.why503.paymentservice.global.client.dto.request;

/**
 * 외부 계정 서비스에 포인트 증감 또는 차감을 요청할 때 사용하는 금액 정보 객체
 */
public record PointUseRequest(
        Long amount
) {
}