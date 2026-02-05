package io.why503.paymentservice.domain.payment.util.exception;

import io.why503.commonbase.exception.payment.domain.PaymentPaymentException;
import io.why503.commonbase.exception.payment.domain.PaymentPointException;
import org.springframework.http.HttpStatus;

public class PaymentNotFound extends PaymentPaymentException {
    public PaymentNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public PaymentNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}