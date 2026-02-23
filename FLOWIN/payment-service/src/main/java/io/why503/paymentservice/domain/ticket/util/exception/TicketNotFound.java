package io.why503.paymentservice.domain.ticket.util.exception;

import io.why503.commonbase.exception.payment.domain.PaymentPaymentException;
import io.why503.commonbase.exception.payment.domain.PaymentTicketException;
import org.springframework.http.HttpStatus;

public class TicketNotFound extends PaymentTicketException {
    public TicketNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public TicketNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}