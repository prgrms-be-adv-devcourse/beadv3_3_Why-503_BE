package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.paymentservice.domain.booking.model.dto.response.TicketResponse;
import io.why503.paymentservice.domain.booking.model.entity.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 예매 엔티티와 응답 DTO 간의 데이터 변환을 담당하는 컴포넌트
 */
@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final TicketMapper ticketMapper;

    // 예매 엔티티를 하위 티켓 목록을 포함한 응답 객체로 변환
    public BookingResponse entityToResponse(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("변환할 Booking Entity는 필수입니다.");
        }

        var ticketEntities = booking.getTickets();

        List<TicketResponse> ticketResponses = (ticketEntities == null || ticketEntities.isEmpty())
                ? Collections.emptyList()
                : ticketEntities.stream()
                .map(ticket -> ticketMapper.entityToResponse(ticket))
                .toList();

        return new BookingResponse(
                booking.getSq(),
                booking.getOrderId(),
                booking.getStatus().name(),
                booking.getStatus().getDescription(),
                booking.getOriginalAmount(),
                booking.getFinalAmount(),
                booking.getCancelReason(),
                booking.getCreatedDt(),
                ticketResponses
        );
    }
}