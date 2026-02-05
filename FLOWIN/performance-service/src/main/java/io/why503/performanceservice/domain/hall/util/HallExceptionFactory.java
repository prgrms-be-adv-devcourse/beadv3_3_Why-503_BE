package io.why503.performanceservice.domain.hall.util;

import io.why503.commonbase.exception.CustomException;
import io.why503.performanceservice.domain.hall.util.exception.HallBadRequest;
import io.why503.performanceservice.domain.hall.util.exception.HallForbidden;
import io.why503.performanceservice.domain.hall.util.exception.HallConflict;
import io.why503.performanceservice.domain.hall.util.exception.HallNotFound;


public final class HallExceptionFactory {

    // 400 Bad Request
    public static CustomException hallBadRequest(String message){
        return new HallBadRequest(message);
    }
    public static CustomException  hallBadRequest(Throwable cause){
        return new HallBadRequest(cause);
    }

    // 404 Not Found
    public static CustomException  hallNotFound(String message) {
        return new HallNotFound(message);
    }
    public static CustomException  hallNotFound(Throwable cause) {
        return new HallNotFound(cause);
    }

    // 403 Forbidden
    public static CustomException  hallForbidden(String message) {
        return new HallForbidden(message);
    }
    public static CustomException  hallForbidden(Throwable cause) {
        return new HallForbidden(cause);
    }

    // 409 Conflict
    public static CustomException  hallConflict(String message) {return new HallConflict(message);}
    public static CustomException  hallConflict(Throwable cause) {
        return new HallConflict(cause);
    }
}