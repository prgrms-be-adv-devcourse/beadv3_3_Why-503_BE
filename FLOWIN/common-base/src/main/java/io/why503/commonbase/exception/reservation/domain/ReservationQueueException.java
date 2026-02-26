package io.why503.commonbase.exception.reservation.domain;

import io.why503.commonbase.exception.reservation.ReservationException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(QU) + HttpStatus
 */
public class ReservationQueueException extends ReservationException {
    public ReservationQueueException(String message, HttpStatus status) {
        super(message, "QU-" + status.value(), status);
    }
    public ReservationQueueException(Throwable cause, HttpStatus status) {
        super(cause, "QU-" + status.value(), status);
    }
}
