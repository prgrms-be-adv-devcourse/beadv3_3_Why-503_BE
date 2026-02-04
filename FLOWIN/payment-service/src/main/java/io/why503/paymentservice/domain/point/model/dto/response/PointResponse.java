package io.why503.paymentservice.domain.point.model.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * 포인트 충전 요청의 처리 결과와 상태 정보를 전달하는 응답 객체
 */
public record PointResponse(
        @NotNull
        Long sq,
        @NotBlank
        String orderId,
        @NotNull
        Long chargeAmount,
        @NotBlank
        String status,
        @NotBlank
        String statusDescription,
        @NotNull
        LocalDateTime createdDt
) {
}