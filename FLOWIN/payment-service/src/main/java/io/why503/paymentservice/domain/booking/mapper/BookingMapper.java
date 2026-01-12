package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.BookingReqDto;
import io.why503.paymentservice.domain.booking.model.ett.Ticket;
import io.why503.paymentservice.domain.booking.model.ett.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    // Request DTO -> Booking Entity 변환 로직 분리
    public Booking toEntity(BookingReqDto req) {
        // 1. Booking 본체 생성
        Booking booking = Booking.builder()
                .userSq(req.getUserSq())
                .bookingAmount(req.getTotalAmount())
                // DB 제약조건 방어용 초기값 설정
                .totalAmount(req.getTotalAmount())
                .bookingStatus(0) // 0:선점
                .build();

        // 2. Ticket 리스트 생성 및 연결
        if (req.getTickets() != null) {
            for (BookingReqDto.TicketDto item : req.getTickets()) {
                Ticket ticket = Ticket.builder()
                        .showingSeatSq(item.getShowingSeatSq())
                        .originalPrice(item.getOriginalPrice())
                        .finalPrice(item.getFinalPrice())
                        .ticketStatus(0) // 0:발권
                        .build();

                // 부모-자식 연관관계 설정
                booking.addTicket(ticket);
            }
        }

        return booking;
    }
}