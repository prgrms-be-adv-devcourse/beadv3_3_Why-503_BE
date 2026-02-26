package io.why503.performanceservice.domain.show.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceShowException;

import org.springframework.http.HttpStatus;

public class ShowConflict extends PerformanceShowException {
    public ShowConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ShowConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}