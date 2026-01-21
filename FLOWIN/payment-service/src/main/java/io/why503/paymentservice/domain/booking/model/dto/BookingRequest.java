package io.why503.paymentservice.domain.booking.model.dto;

import java.util.List;

public record BookingRequest(
        Integer usedPoint,
        List<TicketRequest> tickets
) {
}