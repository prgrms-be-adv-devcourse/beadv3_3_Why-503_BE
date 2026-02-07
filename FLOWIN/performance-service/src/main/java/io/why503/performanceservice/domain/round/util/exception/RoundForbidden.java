package io.why503.performanceservice.domain.round.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceRoundException;

import org.springframework.http.HttpStatus;

public class RoundForbidden extends PerformanceRoundException {
    public RoundForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public RoundForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}