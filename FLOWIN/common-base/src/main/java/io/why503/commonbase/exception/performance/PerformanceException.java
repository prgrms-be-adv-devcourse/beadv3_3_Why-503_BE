package io.why503.commonbase.exception.performance;

import io.why503.commonbase.exception.CustomException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 첫번째 코드(PE)
 */
public class PerformanceException extends CustomException {
    protected PerformanceException(String message, String code, HttpStatus status) {
        super(message, "PE-" + code, status);
    }
    protected PerformanceException(Throwable cause, String code, HttpStatus status) {
        super(cause, "PE-" + code, status);
    }
}
