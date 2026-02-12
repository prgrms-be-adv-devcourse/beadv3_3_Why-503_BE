package io.why503.paymentservice.domain.point.util.exception;

import io.why503.commonbase.exception.payment.domain.PaymentPointException;
import org.springframework.http.HttpStatus;

public class PointConflict extends PaymentPointException {
    public PointConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public PointConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}