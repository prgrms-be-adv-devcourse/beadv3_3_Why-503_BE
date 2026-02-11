package io.why503.performanceservice.domain.showseat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceShowSeatException;
import org.springframework.http.HttpStatus;

public class ShowSeatConflict extends PerformanceShowSeatException {
    public ShowSeatConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ShowSeatConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}