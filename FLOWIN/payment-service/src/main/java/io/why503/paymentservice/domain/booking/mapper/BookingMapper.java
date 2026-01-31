package io.why503.paymentservice.domain.booking.mapper;

import io.why503.paymentservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.paymentservice.domain.booking.model.dto.response.TicketResponse;
import io.why503.paymentservice.domain.booking.model.entity.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final TicketMapper ticketMapper;

    /**
     * Booking Entity -> BookingResponse DTO 변환
     * - Booking 내부에 연관된 Tickets 목록을 자동으로 추출하여 함께 매핑합니다.
     */
    public BookingResponse entityToResponse(Booking booking) {
        // 해피 패스 금지: Entity 필수 검증
        if (booking == null) {
            throw new IllegalArgumentException("변환할 Booking Entity는 필수입니다.");
        }

        // 1. 엔티티 내의 연관관계에서 티켓 목록 추출
        // (JPA 지연 로딩 발생 시점에서 쿼리가 실행됨 - @Transactional 필요)
        var ticketEntities = booking.getTickets();

        // 2. Tickets Null Safe 처리 및 DTO 변환
        List<TicketResponse> ticketResponses = (ticketEntities == null || ticketEntities.isEmpty())
                ? Collections.emptyList()
                : ticketEntities.stream()
                .map(ticket -> ticketMapper.entityToResponse(ticket)) // 메서드 참조 지양, 명시적 호출 유지
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