package io.why503.performanceservice.domain.show.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceShowException;

import org.springframework.http.HttpStatus;

public class ShowBadRequest extends PerformanceShowException {
    public ShowBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public ShowBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}