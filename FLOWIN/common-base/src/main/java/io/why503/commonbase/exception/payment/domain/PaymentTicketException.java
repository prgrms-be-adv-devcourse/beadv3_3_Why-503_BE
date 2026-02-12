package io.why503.commonbase.exception.payment.domain;

import io.why503.commonbase.exception.payment.PaymentException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(BO) + HttpStatus
 */
public class PaymentTicketException extends PaymentException {
    public PaymentTicketException(String message, HttpStatus status) {
        super(message, "TI-" + status.value(), status);
    }
    public PaymentTicketException(Throwable cause, HttpStatus status) {
        super(cause, "TI-" + status.value(), status);
    }
}
