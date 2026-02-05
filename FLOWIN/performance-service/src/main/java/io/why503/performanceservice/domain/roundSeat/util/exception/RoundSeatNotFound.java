package io.why503.performanceservice.domain.roundSeat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class RoundSeatNotFound extends PerformanceHallException {
    public RoundSeatNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public RoundSeatNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}