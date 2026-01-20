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
                .roundSeatSq(ticket.getRoundSeatSq())
                .ticketUuid(ticket.getTicketUuid())

                // 공연 정보
                .showName(ticket.getShowName())
                .concertHallName(ticket.getConcertHallName())
                .roundDate(ticket.getRoundDate())

                // [수정] 필드명 일치시키기
                .grade(ticket.getGrade())               // 엔티티의 grade
                .seatArea(ticket.getSeatArea())         // 엔티티의 seatArea
                .areaSeatNumber(ticket.getAreaSeatNumber()) // 엔티티의 areaSeatNumber

                .price(ticket.getFinalPrice())
                .status(ticket.getTicketStatus().getDescription())
                .build();
    }
}