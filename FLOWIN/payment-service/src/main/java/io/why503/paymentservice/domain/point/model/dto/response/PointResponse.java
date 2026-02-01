package io.why503.paymentservice.domain.point.model.dto.response;

import java.time.LocalDateTime;

/**
 * 포인트 충전 요청의 처리 결과와 상태 정보를 전달하는 응답 객체
 */
public record PointResponse(
        Long sq,
        String orderId,
        Long chargeAmount,
        String status,
        String statusDescription,
        LocalDateTime createdDt
) {
}