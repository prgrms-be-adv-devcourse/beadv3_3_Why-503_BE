package io.why503.paymentservice.domain.point.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 포인트 충전 요청 시 필요한 충전 금액 정보를 담는 객체
 */
public record PointRequest(
        @NotNull(message = "충전 금액은 필수입니다.")
        @Positive(message = "충전 금액은 0원보다 커야 합니다.")
        Long chargeAmount
) {
}