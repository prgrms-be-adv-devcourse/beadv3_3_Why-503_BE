package io.why503.performanceservice.domain.seat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceRoundSeatException;

import org.springframework.http.HttpStatus;

public class SeatConflict extends PerformanceRoundSeatException {
    public SeatConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public SeatConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}