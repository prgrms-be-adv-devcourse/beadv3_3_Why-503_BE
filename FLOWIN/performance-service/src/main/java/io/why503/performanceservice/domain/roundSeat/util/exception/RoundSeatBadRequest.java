package io.why503.performanceservice.domain.roundSeat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceRoundSeatException;
import org.springframework.http.HttpStatus;

public class RoundSeatBadRequest extends PerformanceRoundSeatException {
    public RoundSeatBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public RoundSeatBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}