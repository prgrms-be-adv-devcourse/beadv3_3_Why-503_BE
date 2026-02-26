package io.why503.commonbase.exception.performance.domain;

import io.why503.commonbase.exception.performance.PerformanceException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(RO) + HttpStatus
 */
public class PerformanceRoundException extends PerformanceException {
    public PerformanceRoundException(String message, HttpStatus status) {
        super(message, "RO-" + status.value(), status);
    }
    public PerformanceRoundException(Throwable cause, HttpStatus status) {
        super(cause, "RO-" + status.value(), status);
    }
}
