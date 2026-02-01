package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.response.TicketResponse;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import org.springframework.stereotype.Component;

/**
 * 티켓 엔티티의 데이터를 응답용 DTO로 변환하는 컴포넌트
 */
@Component
public class TicketMapper {

    // 개별 티켓 엔티티의 스냅샷 정보를 응답 객체로 변환
    public TicketResponse entityToResponse(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("변환할 Ticket Entity는 필수입니다.");
        }

        return new TicketResponse(
                ticket.getSq(),
                ticket.getRoundSeatSq(),
                ticket.getUuid(),
                ticket.getShowName(),
                ticket.getHallName(),
                ticket.getRoundDt(),
                ticket.getSeatGrade(),
                ticket.getSeatArea(),
                ticket.getSeatAreaNum(),
                ticket.getOriginalPrice(),
                ticket.getDiscountPolicy().name(),
                ticket.getDiscountPolicy().getDescription(),
                ticket.getDiscountAmount(),
                ticket.getFinalPrice(),
                ticket.getStatus().name(),
                ticket.getStatus().getDescription()
        );
    }
}