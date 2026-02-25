package io.why503.paymentservice.domain.ticket.util.exception;

import io.why503.commonbase.exception.payment.domain.PaymentTicketException;
import org.springframework.http.HttpStatus;

public class TicketForbidden extends PaymentTicketException {
    public TicketForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public TicketForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}