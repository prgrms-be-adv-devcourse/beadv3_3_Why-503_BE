package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.BookingRequest;
import io.why503.paymentservice.domain.booking.model.dto.BookingResponse;
import io.why503.paymentservice.domain.booking.model.dto.TicketRequest;
import io.why503.paymentservice.domain.booking.model.dto.TicketResponse;
import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import io.why503.paymentservice.domain.booking.model.vo.BookingStatus;
import io.why503.paymentservice.domain.booking.model.vo.TicketStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final TicketMapper ticketMapper;

    // 1. ReqDto -> Entity 변환
    public Booking requestToEntity(BookingRequest bookingRequest) {
        Booking booking = Booking.builder()
                .userSq(bookingRequest.getUserSq()) //
                .bookingAmount(bookingRequest.getTotalAmount())
                .totalAmount(bookingRequest.getTotalAmount())
                .usedPoint(bookingRequest.getUsedPoint())// 포인트 기록!
                .bookingStatus(BookingStatus.PENDING)
                .build();

        if (bookingRequest.getTickets() != null) {
            for (TicketRequest item : bookingRequest.getTickets()) {
                Ticket ticket = Ticket.builder()
                        .showingSeatSq(item.getShowingSeatSq())
                        .originalPrice(item.getOriginalPrice())
                        .finalPrice(item.getFinalPrice())
                        .ticketStatus(TicketStatus.RESERVED)
                        .build();
                booking.addTicket(ticket);
            }
        }
        return booking;
    }

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
                .bookingAmount(booking.getBookingAmount())
                .bookingDt(booking.getBookingDt())
                .tickets(list)
                .build();
    }
}