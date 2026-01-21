package io.why503.paymentservice.domain.booking.model.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record BookingRequest(
        Integer usedPoint,
        List<TicketRequest> tickets
) {
}