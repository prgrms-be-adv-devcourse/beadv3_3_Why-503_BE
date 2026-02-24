package io.why503.reservationservice.domain.queue.util;

import io.why503.commonbase.exception.CustomException;
import io.why503.reservationservice.domain.queue.util.exception.QueueBadRequest;
import io.why503.reservationservice.domain.queue.util.exception.QueueConflict;
import io.why503.reservationservice.domain.queue.util.exception.QueueForbidden;
import io.why503.reservationservice.domain.queue.util.exception.QueueUnauthorized;

public final class QueueExceptionFactory {

    private QueueExceptionFactory() {}

    // 400 Bad Request
    public static CustomException queueBadRequest(String message) {
        return new QueueBadRequest(message);
    }

    public static CustomException queueBadRequest(Throwable cause) {
        return new QueueBadRequest(cause);
    }

    // 401 Unauthorized
    public static CustomException queueUnauthorized(String message) {
        return new QueueUnauthorized(message);
    }

    public static CustomException queueUnauthorized(Throwable cause) {
        return new QueueUnauthorized(cause);
    }

    // 403 Forbidden
    public static CustomException queueForbidden(String message) {
        return new QueueForbidden(message);
    }

    public static CustomException queueForbidden(Throwable cause) {
        return new QueueForbidden(cause);
    }

    // 409 Conflict
    public static CustomException queueConflict(String message) {
        return new QueueConflict(message);
    }

    public static CustomException queueConflict(Throwable cause) {
        return new QueueConflict(cause);
    }
}