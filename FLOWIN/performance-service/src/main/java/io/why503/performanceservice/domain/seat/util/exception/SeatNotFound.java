package io.why503.performanceservice.domain.seat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class SeatNotFound extends PerformanceHallException {
    public SeatNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public SeatNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}