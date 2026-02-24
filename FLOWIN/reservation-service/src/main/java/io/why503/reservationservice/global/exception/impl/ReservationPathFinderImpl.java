package io.why503.reservationservice.global.exception.impl;

import io.why503.commonbase.exception.CustomException;
import io.why503.commonbase.exception.PathFinder;
import io.why503.reservationservice.domain.booking.util.BookingExceptionFactory;
import io.why503.reservationservice.domain.entry.util.EntryTokenExceptionFactory;
import io.why503.reservationservice.domain.queue.util.QueueExceptionFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ReservationPathFinderImpl implements PathFinder {

    @Override
    public CustomException findPath(HttpServletRequest request, String message, HttpStatus status) {

        String s = request.getRequestURI().split("/")[1];

        return switch (s) {
            case "bookings" -> BookingExceptionFactory.bookingBadRequest(message);
            case "entry" -> EntryTokenExceptionFactory.entryTokenBadRequest(message);
            case "queue" -> QueueExceptionFactory.queueBadRequest(message);
            default -> null;
        };
    }
}