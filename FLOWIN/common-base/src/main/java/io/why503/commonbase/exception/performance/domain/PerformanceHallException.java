package io.why503.commonbase.exception.performance.domain;

import io.why503.commonbase.exception.performance.PerformanceException;

/**
 * 여기서 두번째 코드(HA) + HttpStatus
 */
public class PerformanceHallException extends PerformanceException {
    public PerformanceHallException(String message, String status) {
        super(message, "HA-" + status);
    }
}
