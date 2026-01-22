package io.why503.paymentservice.global.client.dto;

import java.time.LocalDateTime;

public record RoundSeatResponse(
        Long roundSeatSq, // 회차별 좌석 ID
        Integer price,    // 티켓 가격

        // 공연 정보
        String showName,
        String concertHallName,
        LocalDateTime roundDate,

        // 좌석 정보
        String grade,
        String seatArea,
        Integer areaSeatNumber
) {

}