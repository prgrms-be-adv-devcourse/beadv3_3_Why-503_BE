package io.why503.performanceservice.domain.seat.Model.Dto.Cmd;

import lombok.Getter;

@Getter
public class SeatAreaCreateCmd {

    /**
     * 좌석 구역명 (ex: A, B, VIP)
     */
    private final String seatArea;

    // 해당 구역의 좌석 개수
    private final int seatCount;

    public SeatAreaCreateCmd(String seatArea, int seatCount) {
        this.seatArea = seatArea;
        this.seatCount = seatCount;
    }
}