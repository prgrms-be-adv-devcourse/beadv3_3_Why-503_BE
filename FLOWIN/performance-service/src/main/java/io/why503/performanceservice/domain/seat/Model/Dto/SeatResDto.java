package io.why503.performanceservice.domain.seat.model.dto;

import io.why503.performanceservice.domain.seat.model.entity.SeatEntity;
import lombok.Builder;
import lombok.Getter;

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
    public static SeatResDto from(SeatEntity seat) {
        return SeatResDto.builder()
                .seatSq(seat.getSeatSq())
                .seatArea(seat.getSeatArea())
                .areaSeatNo(seat.getAreaSeatNo())
                .seatNo(seat.getSeatNo())
                .build();
    }
}
