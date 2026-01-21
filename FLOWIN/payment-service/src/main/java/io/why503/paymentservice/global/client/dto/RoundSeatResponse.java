package io.why503.paymentservice.global.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회차 좌석 정보 응답 DTO
 * - Performance Service로부터 받아온 공연 및 좌석 상세 정보입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoundSeatResponse {

    private Long roundSeatSq; // 회차별 좌석 ID
    private Integer price;    // 티켓 가격

    // 공연 정보
    private String showName;
    private String concertHallName;
    private LocalDateTime roundDate;

    // 좌석 정보
    private String grade;
    private String seatArea;
    private Integer areaSeatNumber;

    public String getFormattedSeatNo() {
        return this.seatArea + "-" + this.areaSeatNumber;
    }
}