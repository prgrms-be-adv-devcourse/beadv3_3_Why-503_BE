package io.why503.performanceservice.domain.round.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class RoundForbidden extends PerformanceHallException {
    public RoundForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public RoundForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}