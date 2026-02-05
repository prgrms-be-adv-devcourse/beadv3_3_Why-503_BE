package io.why503.performanceservice.domain.roundSeat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class RoundSeatForbidden extends PerformanceHallException {
    public RoundSeatForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public RoundSeatForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}