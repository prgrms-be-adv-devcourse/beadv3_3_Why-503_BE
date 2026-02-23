package io.why503.paymentservice.domain.settlement.util.exception;

import io.why503.commonbase.exception.payment.domain.PaymentPaymentException;
import org.springframework.http.HttpStatus;

public class PaymentConflict extends PaymentPaymentException {
    public PaymentConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public PaymentConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}