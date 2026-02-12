package io.why503.performanceservice.domain.showseat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceShowSeatException;
import org.springframework.http.HttpStatus;

public class ShowSeatBadRequest extends PerformanceShowSeatException {
    public ShowSeatBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public ShowSeatBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}