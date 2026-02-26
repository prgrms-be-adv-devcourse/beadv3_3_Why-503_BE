package io.why503.commonbase.model.dto;

import io.why503.commonbase.exception.CustomException;

import java.util.UUID;

public record ExceptionResponse(
        String id,
        String message,
        String code
) {
    public ExceptionResponse(CustomException e) {
        this(e.getId(), e.getMessage(), e.getCode());
    }
    public ExceptionResponse(Exception e) {
        this(UUID.randomUUID().toString(), e.getMessage(), "unknown");
    }
}
