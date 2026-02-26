package io.why503.performanceservice.domain.roundSeat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceRoundSeatException;
import org.springframework.http.HttpStatus;

public class RoundSeatConflict extends PerformanceRoundSeatException {
    public RoundSeatConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public RoundSeatConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}