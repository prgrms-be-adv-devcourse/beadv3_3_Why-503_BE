package io.why503.performanceservice.domain.hall.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class HallBadRequest extends PerformanceHallException {
    public HallBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public HallBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}