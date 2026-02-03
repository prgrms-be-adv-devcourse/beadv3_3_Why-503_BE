package io.why503.performanceservice.domain.roundSeat.model.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SeatReserveResponse(
        Long roundSeatSq,       // 티켓과 연결될 ID
        String roundSeatStatus, // 상태
        Long price,          // 가격
        String grade,           // 등급
        String seatArea,        // 구역
        Integer seatAreaNumber,  // 번호

        String showName,        // 공연명
        String hallName, // 공연장명
        LocalDateTime roundDt // 회차일시
) {
}