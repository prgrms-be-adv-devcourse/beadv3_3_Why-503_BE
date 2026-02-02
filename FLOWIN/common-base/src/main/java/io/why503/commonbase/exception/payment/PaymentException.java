package io.why503.commonbase.exception.payment;

import io.why503.commonbase.exception.CustomException;
/**
 * 여기서 첫번째 코드(PA)
 */
public class PaymentException extends CustomException {
    protected PaymentException(String message, String code) {
        super(message, "PA-" + code);
    }
}
