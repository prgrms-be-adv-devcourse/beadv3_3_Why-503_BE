package io.why503.performanceservice.domain.roundSeat.util;

import io.why503.commonbase.exception.CustomException;
import io.why503.performanceservice.domain.roundSeat.util.exception.RoundSeatBadRequest;
import io.why503.performanceservice.domain.roundSeat.util.exception.RoundSeatConflict;
import io.why503.performanceservice.domain.roundSeat.util.exception.RoundSeatForbidden;
import io.why503.performanceservice.domain.roundSeat.util.exception.RoundSeatNotFound;


public final class RoundSeatExceptionFactory {

    // 400 Bad Request
    public static CustomException roundSeatBadRequest(String message){
        return new RoundSeatBadRequest(message);
    }
    public static CustomException roundSeatBadRequest(Throwable cause){
        return new RoundSeatBadRequest(cause);
    }

    // 404 Not Found
    public static CustomException roundSeatNotFound(String message) {
        return new RoundSeatNotFound(message);
    }
    public static CustomException roundSeatNotFound(Throwable cause) {
        return new RoundSeatNotFound(cause);
    }

    // 403 Forbidden
    public static CustomException roundSeatForbidden(String message) {
        return new RoundSeatForbidden(message);
    }
    public static CustomException roundSeatForbidden(Throwable cause) {
        return new RoundSeatForbidden(cause);
    }

    // 409 Conflict
    public static CustomException roundSeatConflict(String message) {return new RoundSeatConflict(message);}
    public static CustomException roundSeatConflict(Throwable cause) {
        return new RoundSeatConflict(cause);
    }
}