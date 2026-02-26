package io.why503.commonbase.exception.performance.domain;

import io.why503.commonbase.exception.performance.PerformanceException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(RS) + HttpStatus
 */
public class PerformanceRoundSeatException extends PerformanceException {
    public PerformanceRoundSeatException(String message, HttpStatus status) {
        super(message, "RS-" + status.value(), status);
    }
    public PerformanceRoundSeatException(Throwable cause, HttpStatus status) {
        super(cause, "RS-" + status.value(), status);
    }
}
