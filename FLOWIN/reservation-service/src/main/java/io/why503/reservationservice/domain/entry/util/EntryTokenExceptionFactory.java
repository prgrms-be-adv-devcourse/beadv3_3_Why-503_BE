package io.why503.reservationservice.domain.entry.util;

import io.why503.commonbase.exception.CustomException;
import io.why503.reservationservice.domain.entry.util.exception.EntryTokenBadRequest;
import io.why503.reservationservice.domain.entry.util.exception.EntryTokenConflict;
import io.why503.reservationservice.domain.entry.util.exception.EntryTokenForbidden;
import io.why503.reservationservice.domain.entry.util.exception.EntryTokenUnauthorized;

public final class EntryTokenExceptionFactory {

    private EntryTokenExceptionFactory() {}

    // 400 Bad Request
    public static CustomException entryTokenBadRequest(String message) {
        return new EntryTokenBadRequest(message);
    }

    public static CustomException entryTokenBadRequest(Throwable cause) {
        return new EntryTokenBadRequest(cause);
    }

    // 401 Unauthorized
    public static CustomException entryTokenUnauthorized(String message) {
        return new EntryTokenUnauthorized(message);
    }

    public static CustomException entryTokenUnauthorized(Throwable cause) {
        return new EntryTokenUnauthorized(cause);
    }

    // 403 Forbidden
    public static CustomException entryTokenForbidden(String message) {
        return new EntryTokenForbidden(message);
    }

    public static CustomException entryTokenForbidden(Throwable cause) {
        return new EntryTokenForbidden(cause);
    }

    // 409 Conflict
    public static CustomException entryTokenConflict(String message) {
        return new EntryTokenConflict(message);
    }

    public static CustomException entryTokenConflict(Throwable cause) {
        return new EntryTokenConflict(cause);
    }
}