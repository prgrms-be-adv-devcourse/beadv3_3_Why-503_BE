package io.why503.performanceservice.domain.showseat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceShowSeatException;
import org.springframework.http.HttpStatus;

public class ShowSeatNotFound extends PerformanceShowSeatException {
    public ShowSeatNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ShowSeatNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}