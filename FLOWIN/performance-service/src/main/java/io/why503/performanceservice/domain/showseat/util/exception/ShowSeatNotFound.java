package io.why503.performanceservice.domain.showseat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class ShowSeatNotFound extends PerformanceHallException {
    public ShowSeatNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ShowSeatNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}