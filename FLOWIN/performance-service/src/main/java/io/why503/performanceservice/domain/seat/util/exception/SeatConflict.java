package io.why503.performanceservice.domain.seat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class SeatConflict extends PerformanceHallException {
    public SeatConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public SeatConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}