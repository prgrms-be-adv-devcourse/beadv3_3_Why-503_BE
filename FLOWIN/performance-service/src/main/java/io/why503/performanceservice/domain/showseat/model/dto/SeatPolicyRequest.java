package io.why503.performanceservice.domain.showseat.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SeatPolicyRequest {

    /**
     * 좌석 구역
     * 예: VIP, A, B
     */
    private String seatArea;

    /**
     * 공연 좌석 등급
     */
    private String grade;

    /**
     * 공연 좌석 가격
     */
    private int price;
}
