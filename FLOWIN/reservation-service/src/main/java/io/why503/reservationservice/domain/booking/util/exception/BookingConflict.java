package io.why503.reservationservice.domain.booking.util.exception;

import io.why503.commonbase.exception.reservation.domain.ReservationBookingException;
import org.springframework.http.HttpStatus;

public class BookingConflict extends ReservationBookingException {
    public BookingConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public BookingConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}