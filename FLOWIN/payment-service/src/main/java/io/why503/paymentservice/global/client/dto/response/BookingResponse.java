package io.why503.paymentservice.global.client.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long sq,
        Long userSq,
        String orderId,
        String status,
        List<Long> roundSeatSqs,
        LocalDateTime createdDt
) {
}