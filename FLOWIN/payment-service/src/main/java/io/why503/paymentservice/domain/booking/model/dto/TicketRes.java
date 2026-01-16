package io.why503.paymentservice.domain.booking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TicketRes {
    private Long ticketSq;
    private Long seatSq;
    private String status;
}