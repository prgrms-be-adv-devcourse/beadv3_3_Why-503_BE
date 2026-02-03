package io.why503.commonbase.exception.payment.domain;

import io.why503.commonbase.exception.payment.PaymentException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(PA) + HttpStatus
 */
public class PaymentPaymentException extends PaymentException {
    public PaymentPaymentException(String message, HttpStatus status) {
        super(message, "PA-" + status.value(), status);
    }
}
