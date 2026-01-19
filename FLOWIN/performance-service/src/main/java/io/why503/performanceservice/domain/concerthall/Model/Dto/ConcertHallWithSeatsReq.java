package io.why503.performanceservice.domain.concerthall.model.dto;

import java.util.List;

import io.why503.performanceservice.domain.seat.model.dto.cmd.SeatAreaCreateCmd;
import lombok.Getter;

/**
 * 공연장 + 좌석 구역 정보를 함께 받기 위한 요청 DTO
 */
@Getter
public class ConcertHallWithSeatsReq {

    /**
     * 공연장 등록 정보
     */
    private ConcertHallReqDto concertHall;

    /**
     * 좌석 구역 생성 정보
     */
    private List<SeatAreaCreateCmd> seatAreas;
}
