package io.why503.commonbase.exception.payment.domain;

import io.why503.commonbase.exception.payment.PaymentException;

/**
 * 여기서 두번째 코드(PA) + HttpStatus
 */
public class PaymentPaymentException extends PaymentException {
    public PaymentPaymentException(String message, String status) {
        super(message, "PA-" + status);
    }
}
