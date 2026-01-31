package io.why503.paymentservice.domain.point.model.dto.response;

import java.time.LocalDateTime;

public record PointResponse(
        Long sq,
        String orderId,
        Long chargeAmount,
        String status,            // 상태 코드 (예: DONE)
        String statusDescription, // 상태 설명 (예: 충전완료)
        LocalDateTime createdDt
) {
}