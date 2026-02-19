package io.why503.reservationservice.domain.booking.util.exception;

import io.why503.commonbase.exception.reservation.domain.ReservationBookingException;
import org.springframework.http.HttpStatus;

public class BookingNotFound extends ReservationBookingException {
    public BookingNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public BookingNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}