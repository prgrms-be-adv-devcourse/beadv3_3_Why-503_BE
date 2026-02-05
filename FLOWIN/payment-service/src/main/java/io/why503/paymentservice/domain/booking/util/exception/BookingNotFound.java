package io.why503.paymentservice.domain.booking.util.exception;

import io.why503.commonbase.exception.payment.domain.PaymentBookingException;
import org.springframework.http.HttpStatus;

public class BookingNotFound extends PaymentBookingException {
    public BookingNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public BookingNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}