package io.why503.commonbase.exception.reservation;

import io.why503.commonbase.exception.CustomException;
import org.springframework.http.HttpStatus;

/**
 * 여기서 첫번째 코드(RE)
 */
public class ReservationException extends CustomException {
    protected ReservationException(String message, String code, HttpStatus status) {
        super(message, "RE-" + code, status);
    }
    protected ReservationException(Throwable cause, String code, HttpStatus status) {
        super(cause, "RE-" + code, status);
    }
}
