package io.why503.performanceservice.domain.seat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceSeatException;
import org.springframework.http.HttpStatus;

public class SeatForbidden extends PerformanceSeatException {
    public SeatForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public SeatForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}