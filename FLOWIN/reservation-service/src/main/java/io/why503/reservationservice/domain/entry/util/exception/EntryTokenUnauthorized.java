package io.why503.reservationservice.domain.entry.util.exception;

import io.why503.commonbase.exception.reservation.domain.ReservationEntryException;
import org.springframework.http.HttpStatus;

public class EntryTokenUnauthorized extends ReservationEntryException {

    public EntryTokenUnauthorized(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public EntryTokenUnauthorized(Throwable cause) {
        super(cause, HttpStatus.UNAUTHORIZED);
    }
}