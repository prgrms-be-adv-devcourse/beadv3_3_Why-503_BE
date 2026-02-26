package io.why503.performanceservice.domain.seat.util;

import io.why503.commonbase.exception.CustomException;
import io.why503.performanceservice.domain.seat.util.exception.SeatBadRequest;
import io.why503.performanceservice.domain.seat.util.exception.SeatConflict;
import io.why503.performanceservice.domain.seat.util.exception.SeatForbidden;
import io.why503.performanceservice.domain.seat.util.exception.SeatNotFound;


public final class SeatExceptionFactory {

    // 400 Bad Request
    public static CustomException seatBadRequest(String message){
        return new SeatBadRequest(message);
    }
    public static CustomException seatBadRequest(Throwable cause){
        return new SeatBadRequest(cause);
    }

    // 404 Not Found
    public static CustomException seatNotFound(String message) {
        return new SeatNotFound(message);
    }
    public static CustomException seatNotFound(Throwable cause) {
        return new SeatNotFound(cause);
    }

    // 403 Forbidden
    public static CustomException seatForbidden(String message) {
        return new SeatForbidden(message);
    }
    public static CustomException seatForbidden(Throwable cause) {
        return new SeatForbidden(cause);
    }

    // 409 Conflict
    public static CustomException seatConflict(String message) {
        return new SeatConflict(message);
    }
    public static CustomException seatConflict(Throwable cause) {
        return new SeatConflict(cause);
    }
}