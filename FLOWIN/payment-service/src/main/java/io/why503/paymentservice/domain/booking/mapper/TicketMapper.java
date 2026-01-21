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
        return TicketResponse.builder()
                .ticketSq(ticket.getTicketSq())
                .roundSeatSq(ticket.getRoundSeatSq())
                .ticketUuid(ticket.getTicketUuid())

                // 공연 및 좌석 정보 매핑
                .showName(ticket.getShowName())
                .concertHallName(ticket.getConcertHallName())
                .roundDate(ticket.getRoundDate())
                .grade(ticket.getGrade())
                .seatArea(ticket.getSeatArea())
                .areaSeatNumber(ticket.getAreaSeatNumber())

                // 가격 및 상태 정보
                .price(ticket.getFinalPrice())
                .status(ticket.getTicketStatus().getDescription())
                .build();
    }
}