package io.why503.commonbase.exception.performance.domain;

import io.why503.commonbase.exception.performance.PerformanceException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(SS) + HttpStatus
 */
public class PerformanceShowSeatException extends PerformanceException {
    public PerformanceShowSeatException(String message, HttpStatus status) {
        super(message, "SS-" + status.value(), status);
    }
}
