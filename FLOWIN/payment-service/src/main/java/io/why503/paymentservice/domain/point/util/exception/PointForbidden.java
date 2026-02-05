package io.why503.paymentservice.domain.point.util.exception;

import io.why503.commonbase.exception.payment.domain.PaymentBookingException;
import io.why503.commonbase.exception.payment.domain.PaymentPointException;
import org.springframework.http.HttpStatus;

public class PointForbidden extends PaymentPointException {
    public PointForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public PointForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}