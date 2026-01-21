package io.why503.performanceservice.domain.roundSeats.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatReserveResponse {
    private Long roundSeatSq;       // 티켓과 연결될 ID
    private String roundSeatStatus; // 상태

    private Integer price;          // 가격
    private String grade;           // 등급
    private String seatArea;        // 구역
    private Integer areaSeatNumber; // 번호
}
