package io.why503.reservationservice.domain.booking.util.exception;

import io.why503.commonbase.exception.reservation.domain.ReservationBookingException;
import org.springframework.http.HttpStatus;

public class BookingForbidden extends ReservationBookingException {
    public BookingForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public BookingForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}