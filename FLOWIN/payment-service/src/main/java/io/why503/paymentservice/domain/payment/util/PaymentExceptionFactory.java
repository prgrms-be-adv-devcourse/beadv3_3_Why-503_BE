package io.why503.paymentservice.domain.payment.util;

import io.why503.paymentservice.domain.payment.util.exception.PaymentBadRequest;

public final class PaymentExceptionFactory {

    public static PaymentBadRequest paymentBadRequest(String message){
        return new PaymentBadRequest(message);
    }
    public static PaymentBadRequest paymentBadRequest(Throwable cause){
        return new PaymentBadRequest(cause);
    }

}
