package io.why503.reservationservice.global.client.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 외부 공연 서비스로부터 수신한 좌석 및 공연 정보
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RoundSeatResponse(
        Long roundSeatSq,
        String roundSeatStatus,
        Long price,
        String grade,
        String seatArea,
        Integer seatAreaNum,
        String showName,
        String hallName,
        LocalDateTime roundDt,
        String category,
        String genre
) {
}