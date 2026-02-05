package io.why503.performanceservice.domain.hall.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class HallForbidden extends PerformanceHallException {
    public HallForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public HallForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}