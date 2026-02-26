package io.why503.performanceservice.global.exception;

import io.why503.commonbase.exception.performance.PerformanceException;
import org.springframework.http.HttpStatus;

public class ServiceUnavailable extends PerformanceException {
    public ServiceUnavailable(String message) {
        super(message, "503", HttpStatus.SERVICE_UNAVAILABLE);
    }
    public ServiceUnavailable(Throwable cause) {
        super(cause, "503", HttpStatus.SERVICE_UNAVAILABLE);
    }
}