package io.why503.paymentservice.domain.payment.util;

import io.why503.commonbase.exception.CustomException;
import io.why503.paymentservice.domain.payment.util.exception.PaymentBadRequest;
import io.why503.paymentservice.domain.payment.util.exception.PaymentConflict;
import io.why503.paymentservice.domain.payment.util.exception.PaymentForbidden;
import io.why503.paymentservice.domain.payment.util.exception.PaymentNotFound;
import io.why503.paymentservice.domain.point.util.exception.PointConflict;
import io.why503.paymentservice.domain.point.util.exception.PointForbidden;
import io.why503.paymentservice.domain.point.util.exception.PointNotFound;

public final class PaymentExceptionFactory {

    public static CustomException paymentBadRequest(String message){
        return new PaymentBadRequest(message);
    }
    public static CustomException paymentBadRequest(Throwable cause){
        return new PaymentBadRequest(cause);
    }

    // 404 Not Found
    public static CustomException paymentNotFound(String message) {
        return new PaymentNotFound(message);
    }
    public static CustomException  paymentNotFound(Throwable cause) {
        return new PaymentNotFound(cause);
    }

    // 403 Forbidden
    public static CustomException  paymentForbidden(String message) {
        return new PaymentForbidden(message);
    }
    public static CustomException  paymentForbidden(Throwable cause) {
        return new PaymentForbidden(cause);
    }

    // 409 Conflict
    public static CustomException  paymentConflict(String message) {
        return new PaymentConflict(message);
    }
    public static CustomException  paymentConflict(Throwable cause) {
        return new PaymentConflict(cause);
    }

}
