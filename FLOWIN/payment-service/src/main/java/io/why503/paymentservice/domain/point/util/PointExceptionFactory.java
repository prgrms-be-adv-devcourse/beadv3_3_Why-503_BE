package io.why503.paymentservice.domain.point.util;

import io.why503.commonbase.exception.CustomException;
import io.why503.paymentservice.domain.point.util.exception.PointBadRequest;
import io.why503.paymentservice.domain.point.util.exception.PointConflict;
import io.why503.paymentservice.domain.point.util.exception.PointForbidden;
import io.why503.paymentservice.domain.point.util.exception.PointNotFound;

public final class PointExceptionFactory {

    public static CustomException pointBadRequest(String message){
        return new PointBadRequest(message);
    }
    public static CustomException pointBadRequest(Throwable cause){
        return new PointBadRequest(cause);
    }

    // 404 Not Found
    public static CustomException pointNotFound(String message) {
        return new PointNotFound(message);
    }
    public static CustomException pointNotFound(Throwable cause) {
        return new PointNotFound(cause);
    }

    // 403 Forbidden
    public static CustomException  pointForbidden(String message) {
        return new PointForbidden(message);
    }
    public static CustomException  pointForbidden(Throwable cause) {
        return new PointForbidden(cause);
    }

    // 409 Conflict
    public static CustomException  pointConflict(String message) {
        return new PointConflict(message);
    }
    public static CustomException  pointConflict(Throwable cause) {
        return new PointConflict(cause);
    }

}
