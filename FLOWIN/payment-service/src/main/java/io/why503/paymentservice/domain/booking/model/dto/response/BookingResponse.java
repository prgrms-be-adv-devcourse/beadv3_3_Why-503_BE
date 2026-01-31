package io.why503.paymentservice.domain.booking.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long sq,
        String orderId,
        String status,
        String statusDescription, // 프론트엔드 표시용 한글 설명
        Long originalAmount,
        Long finalAmount,
        String cancelReason,
        LocalDateTime createdDt,
        List<TicketResponse> tickets
) {
}