package io.why503.reservationservice.domain.entry.util.exception;

import io.why503.commonbase.exception.reservation.domain.ReservationEntryException;
import org.springframework.http.HttpStatus;

public class EntryTokenBadRequest extends ReservationEntryException {

    public EntryTokenBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public EntryTokenBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}