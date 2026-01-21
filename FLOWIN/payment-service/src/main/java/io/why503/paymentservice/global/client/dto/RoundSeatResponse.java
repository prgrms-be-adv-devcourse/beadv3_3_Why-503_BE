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
    // 레코드에서도 메서드를 추가할 수 있습니다.
    // 접근자 호출 시 'get'을 떼고 호출하도록 내부 로직을 수정했습니다.
    public String getFormattedSeatNo() {
        return this.seatArea + "-" + this.areaSeatNumber;
    }
}