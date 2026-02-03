package io.why503.commonbase.exception.payment.domain;

import io.why503.commonbase.exception.payment.PaymentException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(BO) + HttpStatus
 */
public class PaymentBookingException extends PaymentException {
    public PaymentBookingException(String message, HttpStatus status) {
        super(message, "BO-" + status.value(), status);
    }
}
