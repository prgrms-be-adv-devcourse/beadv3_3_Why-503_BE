package io.why503.reservationservice.global.exception;

import io.why503.commonbase.exception.reservation.ReservationException;
import org.springframework.http.HttpStatus;

public class ServiceUnavailable extends ReservationException {
    public ServiceUnavailable(String message) {
        super(message, "503", HttpStatus.SERVICE_UNAVAILABLE);
    }
    public ServiceUnavailable(Throwable cause) {
        super(cause, "503", HttpStatus.SERVICE_UNAVAILABLE);
    }
}