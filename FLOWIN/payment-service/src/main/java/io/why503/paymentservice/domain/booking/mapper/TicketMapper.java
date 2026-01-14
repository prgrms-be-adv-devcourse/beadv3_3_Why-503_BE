package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.TicketResDto;
import io.why503.paymentservice.domain.booking.model.ett.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    // Entity -> ResDto 변환
    public TicketResDto toDto(Ticket ticket) {
        return TicketResDto.builder()
                .ticketSq(ticket.getTicketSq())
                .seatSq(ticket.getShowingSeatSq())
                // Enum의 한글 설명을 꺼내서 담습니다
                .status(ticket.getTicketStatus().getDescription())
                .build();
    }
}