package io.why503.performanceservice.global.error;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private ErrorCode errorCode;

    private String message;
}
