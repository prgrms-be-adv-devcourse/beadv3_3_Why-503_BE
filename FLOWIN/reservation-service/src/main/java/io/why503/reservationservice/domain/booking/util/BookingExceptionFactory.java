package io.why503.reservationservice.domain.booking.util;

import io.why503.commonbase.exception.CustomException;
import io.why503.reservationservice.domain.booking.util.exception.BookingBadRequest;
import io.why503.reservationservice.domain.booking.util.exception.BookingConflict;
import io.why503.reservationservice.domain.booking.util.exception.BookingForbidden;
import io.why503.reservationservice.domain.booking.util.exception.BookingNotFound;

public final class BookingExceptionFactory {

    // 400 Bad Request
    public static CustomException bookingBadRequest(String message){
        return new BookingBadRequest(message);
    }
    public static CustomException  bookingBadRequest(Throwable cause){
        return new BookingBadRequest(cause);
    }

    // 404 Not Found
    public static CustomException  bookingNotFound(String message) {
        return new BookingNotFound(message);
    }
    public static CustomException  bookingNotFound(Throwable cause) {
        return new BookingNotFound(cause);
    }

    // 403 Forbidden
    public static CustomException  bookingForbidden(String message) {
        return new BookingForbidden(message);
    }
    public static CustomException  bookingForbidden(Throwable cause) {
        return new BookingForbidden(cause);
    }

    // 409 Conflict
    public static CustomException  bookingConflict(String message) {
        return new BookingConflict(message);
    }
    public static CustomException  bookingConflict(Throwable cause) {
        return new BookingConflict(cause);
    }
}