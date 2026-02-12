package io.why503.reservationservice.domain.booking.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 예매 상세 및 상태 정보를 반환하는 데이터 객체
 * - 예매 식별자, 상태, 선점된 좌석 목록을 포함
 */
public record BookingResponse(
        Long sq,
        Long userSq,
        String orderId,
        String status,
        List<BookingSeatResponse> bookingSeats,
        LocalDateTime createdDt
) {
}