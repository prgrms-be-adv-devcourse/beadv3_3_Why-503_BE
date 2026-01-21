package io.why503.paymentservice.domain.booking.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record TicketResponse(
        Long ticketSq,
        Long roundSeatSq,
        String ticketUuid,
        String showName,
        String concertHallName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime roundDate,
        String grade,
        String seatArea,
        Integer areaSeatNumber,
        Integer price,
        String status
) {
}