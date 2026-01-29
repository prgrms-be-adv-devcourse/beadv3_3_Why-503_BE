package io.why503.paymentservice.domain.booking.model.dto.response;

import io.why503.paymentservice.domain.booking.model.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long bookingSq,
        Long userSq,
        BookingStatus status,
        String orderId,
        Integer originalAmount,
        Integer finalAmount,
        Integer usedPoint,
        Integer pgAmount,
        LocalDateTime reservedAt,
        List<TicketResponse> tickets
) {
}