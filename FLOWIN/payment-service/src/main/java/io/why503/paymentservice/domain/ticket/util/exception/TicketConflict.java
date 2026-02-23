package io.why503.paymentservice.domain.ticket.util.exception;

import io.why503.commonbase.exception.payment.domain.PaymentPaymentException;
import io.why503.commonbase.exception.payment.domain.PaymentTicketException;
import org.springframework.http.HttpStatus;

public class TicketConflict extends PaymentTicketException {
    public TicketConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public TicketConflict(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}