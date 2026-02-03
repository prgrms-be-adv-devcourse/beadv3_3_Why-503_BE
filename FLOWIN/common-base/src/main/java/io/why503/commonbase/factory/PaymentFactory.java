package io.why503.commonbase.factory;

import io.why503.commonbase.exception.CustomException;
import io.why503.commonbase.exception.payment.domain.PaymentBookingException;
import io.why503.commonbase.exception.payment.domain.PaymentPaymentException;
import io.why503.commonbase.exception.payment.domain.PaymentPointException;
import org.springframework.http.HttpStatus;

/**
 * 입력 값은 모두
 * String message
 * HttpStatus
 * 로 고정
 */
public final class PaymentFactory {

    private PaymentFactory(){}

    public static CustomException paymentException(String message, HttpStatus status){
        return new PaymentPaymentException(message, status);
    }
    public static CustomException bookingException(String message, HttpStatus status){
        return new PaymentBookingException(message, status);
    }
    public static CustomException pointException(String message, HttpStatus status){
        return new PaymentPointException(message, status);
    }
}
