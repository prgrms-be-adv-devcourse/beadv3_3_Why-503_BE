package io.why503.paymentservice.global.client.dto.response;

import java.time.LocalDateTime;

/**
 * 외부 공연 서비스로부터 수신한 회차별 좌석 상세 정보와 공연 스냅샷 데이터를 담는 객체
 */
public record RoundSeatResponse(
        Long roundSeatSq,       // 티켓과 연결될 ID
        String roundSeatStatus, // 상태
        Long price,          // 가격

        String grade,           // 등급
        String seatArea,        // 구역
        Integer seatAreaNum,  // 번호

        String showName,        // 공연명
        String hallName, // 공연장명
        LocalDateTime roundDt // 회차일시
) {
}