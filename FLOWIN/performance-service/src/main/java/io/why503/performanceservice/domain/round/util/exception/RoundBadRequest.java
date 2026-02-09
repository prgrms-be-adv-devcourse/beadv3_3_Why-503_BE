package io.why503.performanceservice.domain.round.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceRoundException;

import org.springframework.http.HttpStatus;

public class RoundBadRequest extends PerformanceRoundException {
    public RoundBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public RoundBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}