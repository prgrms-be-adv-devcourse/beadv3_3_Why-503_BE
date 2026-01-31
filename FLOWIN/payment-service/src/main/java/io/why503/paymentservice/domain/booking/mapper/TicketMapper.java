package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.response.TicketResponse;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    /**
     * Ticket Entity -> TicketResponse DTO 변환
     */
    public TicketResponse entityToResponse(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("변환할 Ticket Entity는 필수입니다.");
        }

        return new TicketResponse(
                ticket.getSq(),
                ticket.getRoundSeatSq(),
                ticket.getUuid(),

                // [공연 스냅샷]
                ticket.getShowName(),
                ticket.getHallName(),
                ticket.getRoundDt(),

                // [좌석 스냅샷]
                ticket.getSeatGrade(),
                ticket.getSeatArea(),
                ticket.getSeatAreaNum(),

                // [가격 스냅샷]
                ticket.getOriginalPrice(),
                ticket.getDiscountPolicy().name(),
                ticket.getDiscountPolicy().getDescription(),
                ticket.getDiscountAmount(),
                ticket.getFinalPrice(),

                // [상태]
                ticket.getStatus().name(),
                ticket.getStatus().getDescription()
        );
    }
}