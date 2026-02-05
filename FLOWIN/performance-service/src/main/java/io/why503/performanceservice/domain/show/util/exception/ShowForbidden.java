package io.why503.performanceservice.domain.show.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceHallException;
import org.springframework.http.HttpStatus;

public class ShowForbidden extends PerformanceHallException {
    public ShowForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public ShowForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}