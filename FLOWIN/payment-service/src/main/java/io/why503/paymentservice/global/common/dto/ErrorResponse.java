package io.why503.paymentservice.global.common.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 전역 예외 처리 시 반환되는 공통 에러 응답 규격 DTO
 */
@Getter
@Builder
public class ErrorResponse {
    private final boolean success;
    private final String errorCode;
    private final String message;
}