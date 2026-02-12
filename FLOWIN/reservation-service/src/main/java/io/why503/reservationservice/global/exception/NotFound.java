package io.why503.reservationservice.global.exception;

import io.why503.commonbase.exception.reservation.ReservationException;
import org.springframework.http.HttpStatus;

public class NotFound extends ReservationException {
    public NotFound(String message) {
        super(message, "404", HttpStatus.NOT_FOUND);
    }
    public NotFound(Throwable cause) {
        super(cause, "404", HttpStatus.NOT_FOUND);
    }
}
