package io.why503.reservationservice.domain.entry.util.exception;

import io.why503.commonbase.exception.reservation.domain.ReservationEntryException;
import org.springframework.http.HttpStatus;

public class EntryTokenConflict extends ReservationEntryException {

    public EntryTokenConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public EntryTokenConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}