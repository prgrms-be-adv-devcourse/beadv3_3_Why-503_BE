package io.why503.performanceservice.domain.round.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceRoundException;

import org.springframework.http.HttpStatus;

public class RoundConflict extends PerformanceRoundException {
    public RoundConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public RoundConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}