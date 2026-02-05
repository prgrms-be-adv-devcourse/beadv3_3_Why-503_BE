package io.why503.performanceservice.domain.round.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class RoundNotFound extends PerformanceHallException {
    public RoundNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public RoundNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}