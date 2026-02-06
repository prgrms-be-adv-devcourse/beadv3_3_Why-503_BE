package io.why503.reservationservice.domain.booking.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 예매(좌석 선점) 정보 조회 응답 DTO
 * - 내부 로직 없는 Record 사용
 * - BookingSeat 엔티티 리스트를 단순 좌석 ID 목록으로 반환
 */
public record BookingResponse(
        Long sq,
        Long userSq,
        String orderId,
        String status, // BookingStatus.name()
        List<Long> roundSeatSqs, // 선점한 좌석 ID 목록
        LocalDateTime createdDt
) {
}