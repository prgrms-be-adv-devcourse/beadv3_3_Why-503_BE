package io.why503.paymentservice.global.exception;

import io.why503.commonbase.exception.payment.PaymentException;
import org.springframework.http.HttpStatus;

public class ServiceUnavailable extends PaymentException {
    public ServiceUnavailable(String message) {
        super(message, "503", HttpStatus.SERVICE_UNAVAILABLE);
    }
    public ServiceUnavailable(Throwable cause) {
        super(cause, "503", HttpStatus.SERVICE_UNAVAILABLE);
    }
}