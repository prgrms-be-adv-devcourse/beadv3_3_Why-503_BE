package io.why503.performanceservice.domain.showseat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class ShowSeatConflict extends PerformanceHallException {
    public ShowSeatConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ShowSeatConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}