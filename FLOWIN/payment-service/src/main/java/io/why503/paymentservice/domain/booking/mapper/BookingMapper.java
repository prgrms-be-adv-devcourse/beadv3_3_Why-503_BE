package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.BookingResponse;
import io.why503.paymentservice.domain.booking.model.dto.TicketResponse;
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

    /**
     * Booking Entity -> BookingResponse DTO 변환
     */
    public BookingResponse EntityToResponse(Booking booking) {
        // 티켓 목록 변환 (메서드 참조 대신 루프 사용)
        List<TicketResponse> ticketResponses = new ArrayList<>();
        for (Ticket ticket : booking.getTickets()) {
            TicketResponse response = ticketMapper.EntityToResponse(ticket);
            ticketResponses.add(response);
        }

        return BookingResponse.builder()
                .bookingSq(booking.getBookingSq())
                .userSq(booking.getUserSq())
                .bookingStatus(booking.getBookingStatus())

                // 금액 정보 매핑
                .bookingAmount(booking.getBookingAmount())
                .totalAmount(booking.getTotalAmount())
                .usedPoint(booking.getUsedPoint())
                .pgAmount(booking.getPgAmount())

                .bookingDt(booking.getBookingDt())
                .tickets(ticketResponses)
                .build();
    }
}