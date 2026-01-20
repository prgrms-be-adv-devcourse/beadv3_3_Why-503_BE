package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.BookingResponse;
import io.why503.paymentservice.domain.booking.model.dto.TicketResponse;
import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final TicketMapper ticketMapper;

    // 2. Entity -> ResDto 변환
    public BookingResponse EntityToResponse(Booking booking) {
        List<TicketResponse> list = new ArrayList<>();
        for (Ticket ticket : booking.getTickets()) {
            TicketResponse ticketResponse = ticketMapper.EntityToResponse(ticket);
            list.add(ticketResponse);
        }

        return BookingResponse.builder()
                .bookingSq(booking.getBookingSq())
                .userSq(booking.getUserSq())
                .bookingStatus(booking.getBookingStatus())

                // [수정] 상세 금액 정보 매핑
                .bookingAmount(booking.getBookingAmount())
                .totalAmount(booking.getTotalAmount())  // 추가됨
                .usedPoint(booking.getUsedPoint())      // 추가됨
                .pgAmount(booking.getPgAmount())        // 추가됨

                .bookingDt(booking.getBookingDt())
                .tickets(list)
                .build();
    }
}