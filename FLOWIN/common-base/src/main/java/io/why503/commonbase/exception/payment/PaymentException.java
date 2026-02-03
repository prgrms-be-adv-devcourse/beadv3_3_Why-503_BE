package io.why503.commonbase.exception.payment;

import io.why503.commonbase.exception.CustomException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 첫번째 코드(PA)
 */
public class PaymentException extends CustomException {
    protected PaymentException(String message, String code, HttpStatus status) {
        super(message, "PA-" + code, status);
    }
    protected PaymentException(Throwable cause, String code, HttpStatus status) {
        super(cause, "PA-" + code, status);
    }
}
