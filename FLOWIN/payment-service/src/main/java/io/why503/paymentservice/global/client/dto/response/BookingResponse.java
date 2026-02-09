package io.why503.paymentservice.global.client.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 예매 서비스로부터 수신한 주문 및 좌석 점유 정보
 */
public record BookingResponse(
        Long sq,
        Long userSq,
        String orderId,
        String status,
        List<Long> roundSeatSqs,
        LocalDateTime createdDt
) {
}