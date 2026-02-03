package io.why503.commonbase.model.dto;

import io.why503.commonbase.exception.CustomException;

public record ExceptionResponse(
        Throwable cause,
        String message,
        String code
) {
    public ExceptionResponse(CustomException e) {
        this(e.getCause(), e.getMessage(), e.getCode());
    }
}
