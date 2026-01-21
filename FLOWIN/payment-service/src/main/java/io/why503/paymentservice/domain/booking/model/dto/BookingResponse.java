package io.why503.paymentservice.domain.booking.model.dto;

import io.why503.paymentservice.domain.booking.model.vo.BookingStatus;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record BookingResponse(
        Long bookingSq,
        Long userSq,
        BookingStatus bookingStatus,
        Integer bookingAmount,
        Integer totalAmount,
        Integer usedPoint,
        Integer pgAmount,
        LocalDateTime bookingDt,
        List<TicketResponse> tickets
) {
}