package io.why503.commonbase.exception.performance.domain;

import io.why503.commonbase.exception.performance.PerformanceException;

/**
 * 여기서 두번째 코드(SH) + HttpStatus
 */
public class PerformanceShowException extends PerformanceException {
    public PerformanceShowException(String message, String status) {
        super(message, "SH-" + status);
    }
}
