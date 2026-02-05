package io.why503.paymentservice.domain.booking.util.exception;

import io.why503.commonbase.exception.payment.domain.PaymentBookingException;
import org.springframework.http.HttpStatus;

public class BookingForbidden extends PaymentBookingException {
    public BookingForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public BookingForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}