package io.why503.reservationservice.domain.booking.util.exception;

import io.why503.commonbase.exception.reservation.domain.ReservationBookingException;
import org.springframework.http.HttpStatus;

public class BookingBadRequest extends ReservationBookingException {
    public BookingBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public BookingBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}
