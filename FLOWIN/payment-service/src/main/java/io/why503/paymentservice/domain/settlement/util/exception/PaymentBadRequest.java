package io.why503.paymentservice.domain.settlement.util.exception;

import io.why503.commonbase.exception.payment.domain.PaymentPaymentException;
import org.springframework.http.HttpStatus;

public class PaymentBadRequest extends PaymentPaymentException {
    public PaymentBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public PaymentBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}
