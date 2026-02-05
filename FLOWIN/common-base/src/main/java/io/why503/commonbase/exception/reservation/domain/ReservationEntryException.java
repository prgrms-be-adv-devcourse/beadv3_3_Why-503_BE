package io.why503.commonbase.exception.reservation.domain;

import io.why503.commonbase.exception.reservation.ReservationException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(EN) + HttpStatus
 */
public class ReservationEntryException extends ReservationException {
    public ReservationEntryException(String message, HttpStatus status) {
        super(message, "EN-" + status.value(), status);
    }
    public ReservationEntryException(Throwable cause, HttpStatus status) {
        super(cause, "EN-" + status.value(), status);
    }
}
