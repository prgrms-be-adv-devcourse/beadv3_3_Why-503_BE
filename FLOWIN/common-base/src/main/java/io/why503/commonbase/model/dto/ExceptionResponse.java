package io.why503.commonbase.model.dto;

import io.why503.commonbase.exception.CustomException;

public record ExceptionResponse(
        String message,
        String code
) {
    public ExceptionResponse(CustomException e) {
        this(e.getMessage(), e.getCode());
    }
}
