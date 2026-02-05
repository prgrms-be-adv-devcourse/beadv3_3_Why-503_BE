package io.why503.performanceservice.domain.seat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class SeatBadRequest extends PerformanceHallException {
    public SeatBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public SeatBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}