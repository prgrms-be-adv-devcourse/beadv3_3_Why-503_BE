package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.BookingReqDto;
import io.why503.paymentservice.domain.booking.model.dto.BookingResDto;
import io.why503.paymentservice.domain.booking.model.dto.TicketReqDto; // 독립한 DTO 임포트
import io.why503.paymentservice.domain.booking.model.ett.Booking;
import io.why503.paymentservice.domain.booking.model.ett.Ticket;
import io.why503.paymentservice.domain.booking.model.vo.BookingStat;
import io.why503.paymentservice.domain.booking.model.vo.TicketStat;
import lombok.RequiredArgsConstructor; // ★ 추가
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final TicketMapper ticketMapper;

    // 1. ReqDto -> Entity 변환
    public Booking toEntity(BookingReqDto req) {
        Booking booking = Booking.builder()
                .userSq(req.getUserSq())
                .bookingAmount(req.getTotalAmount())
                .totalAmount(req.getTotalAmount())
                .bookingStat(BookingStat.PENDING)
                .build();

        if (req.getTickets() != null) {
            // TicketReqDto가 이제 독립된 클래스이므로 바로 사용 가능
            for (TicketReqDto item : req.getTickets()) {
                Ticket ticket = Ticket.builder()
                        .showingSeatSq(item.getShowingSeatSq())
                        .originalPrice(item.getOriginalPrice())
                        .finalPrice(item.getFinalPrice())
                        .ticketStat(TicketStat.RESERVED)
                        .build();
                booking.addTicket(ticket);
            }
        }
        return booking;
    }

    // 2. Entity -> ResDto 변환
    public BookingResDto toDto(Booking booking) {
        return BookingResDto.builder()
                .bookingSq(booking.getBookingSq())
                .userSq(booking.getUserSq())
                .bookingStat(booking.getBookingStat())
                .bookingAmount(booking.getBookingAmount())
                .bookingDt(booking.getBookingDt())
                //  TicketResDto.from(...) 대신 ticketMapper.toDto(...) 사용
                .tickets(booking.getTickets().stream()
                        .map(ticketMapper::toDto)
                        .collect(Collectors.toList()))
                .build();
    }
}