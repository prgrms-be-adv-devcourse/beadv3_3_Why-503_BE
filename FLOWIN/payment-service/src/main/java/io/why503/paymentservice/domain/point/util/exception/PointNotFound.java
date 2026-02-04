package io.why503.paymentservice.domain.point.util.exception;

import io.why503.commonbase.exception.payment.domain.PaymentBookingException;
import io.why503.commonbase.exception.payment.domain.PaymentPointException;
import org.springframework.http.HttpStatus;

public class PointNotFound extends PaymentPointException {
    public PointNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public PointNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}