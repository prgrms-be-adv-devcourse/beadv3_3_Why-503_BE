package io.why503.commonbase.exception.payment.domain;

import io.why503.commonbase.exception.payment.PaymentException;

/**
 * 여기서 두번째 코드(PO) + HttpStatus
 */
public class PaymentPointException extends PaymentException {
    public PaymentPointException(String message, String status) {
        super(message, "PO-" + status);
    }
}
