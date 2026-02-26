package io.why503.performanceservice.domain.roundSeat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceRoundSeatException;
import org.springframework.http.HttpStatus;

public class RoundSeatNotFound extends PerformanceRoundSeatException {
    public RoundSeatNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public RoundSeatNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}