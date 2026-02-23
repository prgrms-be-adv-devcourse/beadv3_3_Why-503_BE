package io.why503.paymentservice.domain.settlement.util.exception;

import io.why503.commonbase.exception.payment.domain.PaymentPaymentException;
import org.springframework.http.HttpStatus;

public class PaymentForbidden extends PaymentPaymentException {
    public PaymentForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public PaymentForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}