package io.why503.commonbase.exception.performance;

import io.why503.commonbase.exception.CustomException;

/**
 * 여기서 첫번째 코드(PE)
 */
public class PerformanceException extends CustomException {
    protected PerformanceException(String message, String code) {
        super(message, "PE-" + code);
    }
}
