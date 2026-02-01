package io.why503.paymentservice.global.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private final boolean success;
    private final String errorCode;
    private final String message;
}