package io.why503.commonbase.exception.performance.domain;

import io.why503.commonbase.exception.performance.PerformanceException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(SH) + HttpStatus
 */
public class PerformanceShowException extends PerformanceException {
    public PerformanceShowException(String message, HttpStatus status) {
        super(message, "SH-" + status.value(), status);
    }
    public PerformanceShowException(Throwable cause, HttpStatus status) {
        super(cause, "SH-" + status.value(), status);
    }
}
