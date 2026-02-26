package io.why503.performanceservice.domain.showseat.util;

import io.why503.commonbase.exception.CustomException;
import io.why503.performanceservice.domain.showseat.util.exception.ShowSeatBadRequest;
import io.why503.performanceservice.domain.showseat.util.exception.ShowSeatConflict;
import io.why503.performanceservice.domain.showseat.util.exception.ShowSeatForbidden;
import io.why503.performanceservice.domain.showseat.util.exception.ShowSeatNotFound;


public final class ShowSeatExceptionFactory {

    // 400 Bad Request
    public static CustomException showSeatBadRequest(String message){
        return new ShowSeatBadRequest(message);
    }
    public static CustomException showSeatBadRequest(Throwable cause){
        return new ShowSeatBadRequest(cause);
    }

    // 404 Not Found
    public static CustomException showSeatNotFound(String message) {
        return new ShowSeatNotFound(message);
    }
    public static CustomException showSeatNotFound(Throwable cause) {
        return new ShowSeatNotFound(cause);
    }

    // 403 Forbidden
    public static CustomException showSeatForbidden(String message) {
        return new ShowSeatForbidden(message);
    }
    public static CustomException showSeatForbidden(Throwable cause) {
        return new ShowSeatForbidden(cause);
    }

    // 409 Conflict
    public static CustomException showSeatConflict(String message) {
        return new ShowSeatConflict(message);
    }
    public static CustomException showSeatConflict(Throwable cause) {
        return new ShowSeatConflict(cause);
    }
}