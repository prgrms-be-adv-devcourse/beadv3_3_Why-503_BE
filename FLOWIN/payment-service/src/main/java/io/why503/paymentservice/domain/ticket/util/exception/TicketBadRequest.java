package io.why503.paymentservice.domain.ticket.util.exception;

import io.why503.commonbase.exception.payment.domain.PaymentTicketException;
import org.springframework.http.HttpStatus;

public class TicketBadRequest extends PaymentTicketException {
    public TicketBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public TicketBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}
