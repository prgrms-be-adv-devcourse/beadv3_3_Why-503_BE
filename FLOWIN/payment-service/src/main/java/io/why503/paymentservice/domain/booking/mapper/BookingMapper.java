package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.BookingRequest;
import io.why503.paymentservice.domain.booking.model.dto.BookingResponse;
import io.why503.paymentservice.domain.booking.model.dto.TicketRequest; // 독립한 DTO 임포트
import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import io.why503.paymentservice.domain.booking.model.vo.BookingStatus;
import io.why503.paymentservice.domain.booking.model.vo.TicketStatus;
import lombok.RequiredArgsConstructor; // ★ 추가
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final TicketMapper ticketMapper;

    // 1. ReqDto -> Entity 변환
    public Booking toEntity(BookingRequest bookingRequest) {
        Booking booking = Booking.builder()
                .userSq(bookingRequest.getUserSq())
                .bookingAmount(bookingRequest.getTotalAmount())
                .totalAmount(bookingRequest.getTotalAmount())
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
    public BookingResponse toDto(Booking booking) {
        return BookingResponse.builder()
                .bookingSq(booking.getBookingSq())
                .userSq(booking.getUserSq())
                .bookingStatus(booking.getBookingStatus())
                .bookingAmount(booking.getBookingAmount())
                .bookingDt(booking.getBookingDt())
                //  TicketResDto.from(...) 대신 ticketMapper.toDto(...) 사용
                .tickets(booking.getTickets().stream()
                        .map(ticketMapper::toDto)
                        .collect(Collectors.toList()))
                .build();
    }
}