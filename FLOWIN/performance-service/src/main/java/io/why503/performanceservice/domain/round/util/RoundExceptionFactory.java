package io.why503.performanceservice.domain.round.util;

import io.why503.commonbase.exception.CustomException;
import io.why503.performanceservice.domain.round.util.exception.RoundBadRequest;
import io.why503.performanceservice.domain.round.util.exception.RoundConflict;
import io.why503.performanceservice.domain.round.util.exception.RoundForbidden;
import io.why503.performanceservice.domain.round.util.exception.RoundNotFound;


public final class RoundExceptionFactory {

    // 400 Bad Request
    public static CustomException roundBadRequest(String message){
        return new RoundBadRequest(message);
    }
    public static CustomException roundBadRequest(Throwable cause){
        return new RoundBadRequest(cause);
    }

    // 404 Not Found
    public static CustomException roundNotFound(String message) {
        return new RoundNotFound(message);
    }
    public static CustomException roundNotFound(Throwable cause) {
        return new RoundNotFound(cause);
    }

    // 403 Forbidden
    public static CustomException roundForbidden(String message) {
        return new RoundForbidden(message);
    }
    public static CustomException roundForbidden(Throwable cause) {
        return new RoundForbidden(cause);
    }

    // 409 Conflict
    public static CustomException roundConflict(String message) {return new RoundConflict(message);}
    public static CustomException roundConflict(Throwable cause) {
        return new RoundConflict(cause);
    }
}