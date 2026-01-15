package io.why503.performanceservice.domain.seat.Model.Dto;

import lombok.Builder;
import lombok.Getter;

import io.why503.performanceservice.domain.seat.Model.Ett.SeatEtt;

@Getter
@Builder
public class SeatResDto {

    private Long seatSq;
    private String seatArea;
    private Integer areaSeatNo;
    private Integer seatNo;

    /**
     * Entity → DTO 변환
     */
    public static SeatResDto from(SeatEtt seat) {
        return SeatResDto.builder()
                .seatSq(seat.getSeatSq())
                .seatArea(seat.getSeatArea())
                .areaSeatNo(seat.getAreaSeatNo())
                .seatNo(seat.getSeatNo())
                .build();
    }
}
