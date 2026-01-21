package io.why503.performanceservice.domain.roundSeats.model.dto;

import lombok.Builder;

@Builder
public record SeatReserveResponse(
        Long roundSeatSq,       // 티켓과 연결될 ID
        String roundSeatStatus, // 상태
        Integer price,          // 가격
        String grade,           // 등급
        String seatArea,        // 구역
        Integer areaSeatNumber  // 번호
) {
}