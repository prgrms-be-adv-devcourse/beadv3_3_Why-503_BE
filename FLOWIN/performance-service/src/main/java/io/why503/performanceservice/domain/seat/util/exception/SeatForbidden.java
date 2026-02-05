package io.why503.performanceservice.domain.seat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class SeatForbidden extends PerformanceHallException {
    public SeatForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public SeatForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}