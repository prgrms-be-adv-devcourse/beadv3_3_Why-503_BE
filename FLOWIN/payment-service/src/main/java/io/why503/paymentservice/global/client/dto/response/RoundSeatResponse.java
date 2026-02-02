package io.why503.paymentservice.global.client.dto.response;

import java.time.LocalDateTime;

/**
 * 외부 공연 서비스로부터 수신한 회차별 좌석 상세 정보와 공연 스냅샷 데이터를 담는 객체
 */
public record RoundSeatResponse(
        Long roundSeatSq,
        Long price,
        String showName,
        String concertHallName,
        LocalDateTime roundDateTime,
        String grade,
        String seatArea,
        Integer areaSeatNum
) {
}