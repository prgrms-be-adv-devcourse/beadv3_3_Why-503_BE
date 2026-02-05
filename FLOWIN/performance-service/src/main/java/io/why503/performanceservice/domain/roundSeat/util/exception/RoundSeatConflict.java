package io.why503.performanceservice.domain.roundSeat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class RoundSeatConflict extends PerformanceHallException {
    public RoundSeatConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public RoundSeatConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}