package io.why503.performanceservice.domain.roundSeats.model.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SeatReserveResponse(
        Long roundSeatSq,       // 티켓과 연결될 ID
        String roundSeatStatus, // 상태
        Integer price,          // 가격
        String grade,           // 등급
        String seatArea,        // 구역
        Integer areaSeatNumber,  // 번호

        String showName,        // 공연명
        String concertHallName, // 공연장명
        LocalDateTime roundDate // 회차일시
) {
}