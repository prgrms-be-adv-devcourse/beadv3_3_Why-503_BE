package io.why503.commonbase.exception.performance.domain;

import io.why503.commonbase.exception.performance.PerformanceException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(SE) + HttpStatus
 */
public class PerformanceSeatException extends PerformanceException {
    public PerformanceSeatException(String message, HttpStatus status) {
        super(message, "SE-" + status.value(), status);
    }
    public PerformanceSeatException(Throwable cause, HttpStatus status) {
        super(cause, "SE-" + status.value(), status);
    }
}
