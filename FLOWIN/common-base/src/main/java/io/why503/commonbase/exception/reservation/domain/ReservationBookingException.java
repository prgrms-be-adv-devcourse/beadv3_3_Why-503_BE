package io.why503.commonbase.exception.reservation.domain;

import io.why503.commonbase.exception.reservation.ReservationException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 두번째 코드(BO) + HttpStatus
 */
public class ReservationBookingException extends ReservationException {
    public ReservationBookingException(String message, HttpStatus status) {
        super(message, "BO-" + status.value(), status);
    }
    public ReservationBookingException(Throwable cause, HttpStatus status) {
        super(cause, "BO-" + status.value(), status);
    }
}
