package io.why503.paymentservice.global.common.dto;

import lombok.Builder;

/**
 * 전역 예외 처리 시 반환되는 공통 에러 응답 규격 DTO
 */
@Builder
public record ErrorResponse(
        boolean success,
        String errorCode,
        String message
) {
}