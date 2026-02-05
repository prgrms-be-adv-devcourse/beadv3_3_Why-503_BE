package io.why503.performanceservice.domain.show.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class ShowConflict extends PerformanceHallException {
    public ShowConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ShowConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}