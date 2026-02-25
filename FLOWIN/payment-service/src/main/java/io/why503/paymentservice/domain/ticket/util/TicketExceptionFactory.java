package io.why503.paymentservice.domain.ticket.util;

import io.why503.commonbase.exception.CustomException;
import io.why503.paymentservice.domain.ticket.util.exception.TicketBadRequest;
import io.why503.paymentservice.domain.ticket.util.exception.TicketConflict;
import io.why503.paymentservice.domain.ticket.util.exception.TicketForbidden;
import io.why503.paymentservice.domain.ticket.util.exception.TicketNotFound;

public final class TicketExceptionFactory {

    public static CustomException ticketBadRequest(String message){
        return new TicketBadRequest(message);
    }
    public static CustomException ticketBadRequest(Throwable cause){
        return new TicketBadRequest(cause);
    }

    // 404 Not Found
    public static CustomException ticketNotFound(String message) {
        return new TicketNotFound(message);
    }
    public static CustomException  ticketNotFound(Throwable cause) {
        return new TicketNotFound(cause);
    }

    // 403 Forbidden
    public static CustomException  ticketForbidden(String message) {
        return new TicketForbidden(message);
    }
    public static CustomException  ticketForbidden(Throwable cause) {
        return new TicketForbidden(cause);
    }

    // 409 Conflict
    public static CustomException  ticketConflict(String message) {
        return new TicketConflict(message);
    }
    public static CustomException  ticketConflict(Throwable cause) {
        return new TicketConflict(cause);
    }

}
