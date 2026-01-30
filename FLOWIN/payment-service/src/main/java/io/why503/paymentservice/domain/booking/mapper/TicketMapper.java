package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.TicketResponse;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import org.springframework.stereotype.Component;

/**
 * 티켓 엔티티와 DTO 간의 변환을 담당하는 매퍼
 */
@Component
public class TicketMapper {

    /**
     * Ticket Entity -> TicketResponse DTO 변환
     */
    public TicketResponse EntityToResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getTicketSq(),
                ticket.getRoundSeatSq(),
                ticket.getTicketUuid(),
                ticket.getShowName(),
                ticket.getConcertHallName(),
                ticket.getRoundDate(),
                ticket.getGrade(),
                ticket.getSeatArea(),
                ticket.getAreaSeatNumber(),
                ticket.getFinalPrice(),
                ticket.getTicketStatus().getDescription()
        );
    }
}