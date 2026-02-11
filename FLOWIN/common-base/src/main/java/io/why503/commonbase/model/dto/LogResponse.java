package io.why503.commonbase.model.dto;

import io.why503.commonbase.exception.CustomException;

public record LogResponse(
    Throwable cause,
    String code,
    String message,
    String ID
) {
    public LogResponse(CustomException e) {
        this(e.getCause(), e.getCode(), e.getMessage(), e.getId());
    }
}
