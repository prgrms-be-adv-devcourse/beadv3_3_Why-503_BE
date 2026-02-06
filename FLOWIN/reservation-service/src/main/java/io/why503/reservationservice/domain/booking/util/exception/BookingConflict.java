package io.why503.reservationservice.domain.booking.util.exception;

import io.why503.commonbase.exception.payment.domain.PaymentBookingException;
import org.springframework.http.HttpStatus;

public class BookingConflict extends PaymentBookingException {
    public BookingConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public BookingConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}