package io.why503.performanceservice.domain.show.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class ShowBadRequest extends PerformanceHallException {
    public ShowBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public ShowBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}