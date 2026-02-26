package io.why503.reservationservice.domain.queue.util.exception;

import io.why503.commonbase.exception.reservation.domain.ReservationWaitException;
import org.springframework.http.HttpStatus;

public class QueueForbidden extends ReservationWaitException {

    public QueueForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public QueueForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}