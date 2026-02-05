package io.why503.commonbase.exception.reservation.domain;

import io.why503.commonbase.exception.reservation.ReservationException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(WE) + HttpStatus
 */
public class ReservationWaitException extends ReservationException {
    public ReservationWaitException(String message, HttpStatus status) {
        super(message, "WE-" + status.value(), status);
    }
    public ReservationWaitException(Throwable cause, HttpStatus status) {
        super(cause, "WE-" + status.value(), status);
    }
}
