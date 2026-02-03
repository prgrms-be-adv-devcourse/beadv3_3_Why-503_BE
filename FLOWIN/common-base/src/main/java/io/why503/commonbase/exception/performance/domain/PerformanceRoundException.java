package io.why503.commonbase.exception.performance.domain;

import io.why503.commonbase.exception.performance.PerformanceException;

/**
 * 여기서 두번째 코드(RO) + HttpStatus
 */
public class PerformanceRoundException extends PerformanceException {
    public PerformanceRoundException(String message, String status) {
        super(message, "RO-" + status);
    }
}
