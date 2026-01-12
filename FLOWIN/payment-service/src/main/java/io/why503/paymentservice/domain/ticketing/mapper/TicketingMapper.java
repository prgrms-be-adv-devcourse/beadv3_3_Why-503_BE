package io.why503.paymentservice.domain.ticketing.mapper;

import io.why503.reservationservice.domain.ticket.model.dto.TicketingReqDto;
import io.why503.reservationservice.domain.ticket.model.ett.Ticket;
import io.why503.reservationservice.domain.ticket.model.ett.Ticketing;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class TicketingMapper {

    // Request DTO -> Ticketing Entity 변환
    public Ticketing toEntity(TicketingReqDto req) {
        // 1. 예매 본체 생성
        Ticketing ticketing = Ticketing.builder()
                .userSq(req.getUserSq())
                .ticketingPay(req.getTotalAmount())
                .ticketingStat(0) // 초기 상태: 대기
                .build();

        // 2. 하위 티켓 생성 및 연결
        for (TicketingReqDto.TicketItem item : req.getItems()) {
            Ticket ticket = Ticket.builder()
                    .ticketNo(UUID.randomUUID().toString()) // 티켓 번호 생성
                    .ticketPrice(item.getPrice())
                    .showingSeatSq(item.getShowingSeatSq())
                    .ticketStat(0) // 초기 상태: 유효
                    .build();

            // 연관관계 편의 메서드 호출
            ticketing.addTicket(ticket);
        }

        return ticketing;
    }
}