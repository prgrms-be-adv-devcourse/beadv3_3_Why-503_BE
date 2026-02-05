package io.why503.performanceservice.domain.hall.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class HallNotFound extends PerformanceHallException {
    public HallNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public HallNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}