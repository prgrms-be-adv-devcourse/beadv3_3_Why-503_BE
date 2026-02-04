package io.why503.paymentservice.domain.booking.util;

import io.why503.paymentservice.domain.booking.util.exception.BookingBadRequest;

public final class BookingExceptionFactory {

    public static BookingBadRequest bookingBadRequest(String message){
        return new BookingBadRequest(message);
    }
    public static BookingBadRequest bookingBadRequest(Throwable cause){
        return new BookingBadRequest(cause);
    }

}
