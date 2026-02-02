package io.why503.commonbase.exception.performance.domain;

import io.why503.commonbase.exception.performance.PerformanceException;

/**
 * 여기서 두번째 코드(SE) + HttpStatus
 */
public class PerformanceSeatException extends PerformanceException {
    public PerformanceSeatException(String message, String status) {
        super(message, "SE-" + status);
    }
}
