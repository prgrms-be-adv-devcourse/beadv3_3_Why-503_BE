package io.why503.reservationservice.domain.queue.util.exception;

import io.why503.commonbase.exception.reservation.domain.ReservationWaitException;
import org.springframework.http.HttpStatus;

public class QueueUnauthorized extends ReservationWaitException {

    public QueueUnauthorized(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public QueueUnauthorized(Throwable cause) {
        super(cause, HttpStatus.UNAUTHORIZED);
    }
}