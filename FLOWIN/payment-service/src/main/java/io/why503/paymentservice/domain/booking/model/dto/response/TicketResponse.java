package io.why503.paymentservice.domain.booking.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.why503.paymentservice.domain.booking.model.enums.TicketStatus;

import java.time.LocalDateTime;

public record TicketResponse(
        Long ticketSq,
        Long roundSeatSq,
        String uuid,
        String showName,
        String concertHallName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime roundDateTime,
        String grade,
        String seatArea,
        Integer areaSeatNum,
        Integer originalPrice,
        Integer finalPrice,
        TicketStatus status
) {
}