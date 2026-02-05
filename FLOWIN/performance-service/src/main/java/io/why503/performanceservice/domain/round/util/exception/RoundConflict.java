package io.why503.performanceservice.domain.round.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class RoundConflict extends PerformanceHallException {
    public RoundConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public RoundConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}