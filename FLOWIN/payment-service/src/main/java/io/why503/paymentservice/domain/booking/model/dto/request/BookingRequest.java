package io.why503.paymentservice.domain.booking.model.dto.request;

import java.util.List;

public record BookingRequest(
        Integer usedPoint,
        List<TicketRequest> tickets
) {
}