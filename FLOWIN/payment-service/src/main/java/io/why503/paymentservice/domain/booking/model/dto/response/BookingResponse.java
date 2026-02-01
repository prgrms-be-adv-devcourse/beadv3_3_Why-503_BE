package io.why503.paymentservice.domain.booking.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 예매 정보와 소속된 티켓 목록을 전달하는 응답 객체
 */
public record BookingResponse(
        Long sq,
        String orderId,
        String status,
        String statusDescription,
        Long originalAmount,
        Long finalAmount,
        String cancelReason,
        LocalDateTime createdDt,
        List<TicketResponse> tickets
) {
}