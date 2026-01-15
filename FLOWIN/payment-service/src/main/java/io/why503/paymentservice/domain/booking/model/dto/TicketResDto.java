package io.why503.paymentservice.domain.booking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TicketResDto {
    private Long ticketSq;
    private Long seatSq;
    private String status;
}