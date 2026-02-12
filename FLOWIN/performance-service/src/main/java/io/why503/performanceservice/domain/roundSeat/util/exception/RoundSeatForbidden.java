package io.why503.performanceservice.domain.roundSeat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceRoundSeatException;
import org.springframework.http.HttpStatus;

public class RoundSeatForbidden extends PerformanceRoundSeatException {
    public RoundSeatForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public RoundSeatForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}