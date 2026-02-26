package io.why503.performanceservice.domain.showseat.util.exception;

import io.why503.commonbase.exception.performance.domain.PerformanceShowSeatException;
import org.springframework.http.HttpStatus;

public class ShowSeatForbidden extends PerformanceShowSeatException {
    public ShowSeatForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public ShowSeatForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}