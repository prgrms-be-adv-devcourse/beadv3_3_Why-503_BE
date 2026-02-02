package io.why503.commonbase.exception.performance.domain;

import io.why503.commonbase.exception.performance.PerformanceException;

/**
 * 여기서 두번째 코드(RS) + HttpStatus
 */
public class PerformanceRoundSeatException extends PerformanceException {
    public PerformanceRoundSeatException(String message, String status) {
        super(message, "RS-" + status);
    }
}
