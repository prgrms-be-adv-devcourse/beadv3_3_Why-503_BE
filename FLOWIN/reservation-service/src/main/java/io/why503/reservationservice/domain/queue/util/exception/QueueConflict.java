package io.why503.reservationservice.domain.queue.util.exception;

import io.why503.commonbase.exception.reservation.domain.ReservationWaitException;
import org.springframework.http.HttpStatus;

public class QueueConflict extends ReservationWaitException {

    public QueueConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public QueueConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}