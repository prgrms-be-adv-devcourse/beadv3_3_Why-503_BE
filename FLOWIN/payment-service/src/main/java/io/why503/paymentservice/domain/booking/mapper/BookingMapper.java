package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.paymentservice.domain.booking.model.dto.response.TicketResponse;
import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 예매 엔티티와 DTO 간의 변환을 담당하는 매퍼
 */
@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final TicketMapper ticketMapper;

    //Booking Entity -> BookingResponse DTO 변환
    public BookingResponse entityToResponse(Booking booking) {
        // 티켓 목록 변환 (메서드 참조 대신 루프 사용)
        List<TicketResponse> ticketResponses = new ArrayList<>();
        for (Ticket ticket : booking.getTickets()) {
            TicketResponse response = ticketMapper.entityToResponse(ticket);
            ticketResponses.add(response);
        }

        return new BookingResponse(
                booking.getSq(),
                booking.getUserSq(),
                booking.getStatus(),
                booking.getOrderId(),
                booking.getOriginalAmount(),
                booking.getFinalAmount(),
                booking.getUsedPoint(),
                booking.getPgAmount(),
                booking.getReservedAt(),
                ticketResponses
        );
    }
}