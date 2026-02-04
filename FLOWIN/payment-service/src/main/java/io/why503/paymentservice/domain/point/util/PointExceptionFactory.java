package io.why503.paymentservice.domain.point.util;

import io.why503.paymentservice.domain.point.util.exception.PointBadRequest;

public final class PointExceptionFactory {

    public static PointBadRequest pointBadRequest(String message){
        return new PointBadRequest(message);
    }
    public static PointBadRequest pointBadRequest(Throwable cause){
        return new PointBadRequest(cause);
    }

}
