package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.BookingReq;
import io.why503.paymentservice.domain.booking.model.dto.BookingRes;
import io.why503.paymentservice.domain.booking.model.dto.TicketReq; // 독립한 DTO 임포트
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
    public Booking toEntity(BookingReq req) {
        Booking booking = Booking.builder()
                .userSq(req.getUserSq())
                .bookingAmount(req.getTotalAmount())
                .totalAmount(req.getTotalAmount())
                .bookingStatus(BookingStatus.PENDING)
                .build();

        if (req.getTickets() != null) {
            for (TicketReq item : req.getTickets()) {
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
    public BookingRes toDto(Booking booking) {
        return BookingRes.builder()
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