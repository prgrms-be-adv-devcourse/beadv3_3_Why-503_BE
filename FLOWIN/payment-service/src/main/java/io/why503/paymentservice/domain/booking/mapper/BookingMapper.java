package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.BookingReqDto;
import io.why503.paymentservice.domain.booking.model.dto.BookingResDto;
import io.why503.paymentservice.domain.booking.model.dto.TicketDto; // 독립한 DTO 임포트
import io.why503.paymentservice.domain.booking.model.ett.Booking;
import io.why503.paymentservice.domain.booking.model.ett.Ticket;
import io.why503.paymentservice.domain.booking.model.type.BookingStatus;
import io.why503.paymentservice.domain.booking.model.type.TicketStatus;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BookingMapper {

    // 1. ReqDto -> Entity 변환
    public Booking toEntity(BookingReqDto req) {
        Booking booking = Booking.builder()
                .userSq(req.getUserSq())
                .bookingAmount(req.getTotalAmount())
                .totalAmount(req.getTotalAmount())
                .bookingStatus(BookingStatus.PENDING)
                .build();

        if (req.getTickets() != null) {
            // TicketDto가 이제 독립된 클래스이므로 바로 사용 가능
            for (TicketDto item : req.getTickets()) {
                Ticket ticket = Ticket.builder()
                        .showingSeatSq(item.getShowingSeatSq())
                        .originalPrice(item.getOriginalPrice())
                        .finalPrice(item.getFinalPrice())
                        .ticketStatus(TicketStatus.AVAILABLE)
                        .build();
                booking.addTicket(ticket);
            }
        }
        return booking;
    }

    // 2. Entity -> ResDto 변환 (★ 여기가 이사 온 로직)
    public BookingResDto toDto(Booking booking) {
        return BookingResDto.builder()
                .bookingSq(booking.getBookingSq())
                .userSq(booking.getUserSq())
                .bookingStatus(booking.getBookingStatus())
                .bookingAmount(booking.getBookingAmount())
                .bookingDt(booking.getBookingDt())
                .seatSqs(booking.getTickets().stream()
                        .map(Ticket::getShowingSeatSq)
                        .collect(Collectors.toList()))
                .build();
    }
}