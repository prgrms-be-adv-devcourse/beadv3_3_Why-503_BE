package io.why503.reservationservice.domain.booking.model.dto.request;

import java.util.List;

/**
 * 예매 취소 요청 DTO
 * - 전체 취소: roundSeatSqs가 null이거나 비어있음
 * - 부분 취소: roundSeatSqs에 취소할 좌석 ID 포함
 */
public record BookingCancelRequest(
        List<Long> roundSeatSqs,
        String reason
) {
}