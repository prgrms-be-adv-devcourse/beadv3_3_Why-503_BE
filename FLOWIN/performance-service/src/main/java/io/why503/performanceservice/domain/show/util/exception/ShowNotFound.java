package io.why503.performanceservice.domain.show.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceShowException;

import org.springframework.http.HttpStatus;

public class ShowNotFound extends PerformanceShowException {
    public ShowNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ShowNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}