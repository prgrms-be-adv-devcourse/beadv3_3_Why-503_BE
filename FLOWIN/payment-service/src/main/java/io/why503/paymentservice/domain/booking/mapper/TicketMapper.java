package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.TicketRes;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    // Entity -> ResDto 변환
    public TicketRes toDto(Ticket ticket) {
        return TicketRes.builder()
                .ticketSq(ticket.getTicketSq())
                .seatSq(ticket.getShowingSeatSq())
                .status(ticket.getTicketStatus().getDescription())
                .build();
    }
}