package io.why503.paymentservice.global.exception;

import io.why503.commonbase.exception.payment.PaymentException;
import org.springframework.http.HttpStatus;

public class NotFound extends PaymentException {
    public NotFound(String message) {
        super(message, "404", HttpStatus.NOT_FOUND);
    }
    public NotFound(Throwable cause) {
        super(cause, "404", HttpStatus.NOT_FOUND);
    }
}
