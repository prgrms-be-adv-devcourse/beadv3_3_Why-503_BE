package io.why503.performanceservice.domain.hall.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class HallConflict extends PerformanceHallException {
    public HallConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public HallConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}