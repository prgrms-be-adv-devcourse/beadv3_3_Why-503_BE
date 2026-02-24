package io.why503.commonbase.exception.payment.domain;

import io.why503.commonbase.exception.payment.PaymentException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(SE) + HttpStatus
 */
public class PaymentSettlementException extends PaymentException {
    public PaymentSettlementException(String message, HttpStatus status) {
        super(message, "SE-" + status.value(), status);
    }
    public PaymentSettlementException(Throwable cause, HttpStatus status) {
        super(cause, "SE-" + status.value(), status);
    }
}
