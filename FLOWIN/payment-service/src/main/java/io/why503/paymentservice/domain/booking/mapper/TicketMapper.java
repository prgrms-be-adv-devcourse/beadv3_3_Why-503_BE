package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.response.TicketResponse;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import org.springframework.stereotype.Component;

/**
 * 티켓 엔티티와 DTO 간의 변환을 담당하는 매퍼
 */
@Component
public class TicketMapper {

    //Ticket Entity -> TicketResponse DTO 변환
    public TicketResponse entityToResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getSq(),
                ticket.getRoundSeatSq(),
                ticket.getUuid(),
                ticket.getShowName(),
                ticket.getConcertHallName(),
                ticket.getRoundDateTime(),
                ticket.getGrade(),
                ticket.getSeatArea(),
                ticket.getAreaSeatNum(),
                ticket.getOriginalPrice(),
                ticket.getFinalPrice(),
                ticket.getStatus()
        );
    }
}