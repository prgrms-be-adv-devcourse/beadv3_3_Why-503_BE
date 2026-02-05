package io.why503.paymentservice.domain.point.util.exception;

import io.why503.commonbase.exception.payment.domain.PaymentPointException;
import org.springframework.http.HttpStatus;

public class PointBadRequest extends PaymentPointException {
    public PointBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public PointBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}
