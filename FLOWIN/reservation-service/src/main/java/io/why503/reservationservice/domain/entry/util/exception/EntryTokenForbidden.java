package io.why503.reservationservice.domain.entry.util.exception;

import io.why503.commonbase.exception.reservation.domain.ReservationEntryException;
import org.springframework.http.HttpStatus;

public class EntryTokenForbidden extends ReservationEntryException {

    public EntryTokenForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public EntryTokenForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}