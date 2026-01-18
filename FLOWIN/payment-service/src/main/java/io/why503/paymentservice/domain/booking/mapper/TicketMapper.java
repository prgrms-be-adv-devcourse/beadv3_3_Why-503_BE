package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.TicketResponse;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    // Entity -> ResDto 변환
    public TicketResponse EntityToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .ticketSq(ticket.getTicketSq())
                .seatSq(ticket.getShowingSeatSq())
                .status(ticket.getTicketStatus().getDescription())
                .build();
    }
}