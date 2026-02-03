package io.why503.commonbase.exception.payment.domain;

import io.why503.commonbase.exception.payment.PaymentException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(PO) + HttpStatus
 */
public class PaymentPointException extends PaymentException {
    public PaymentPointException(String message, HttpStatus status) {
        super(message, "PO-" + status.value(), status);
    }
}
