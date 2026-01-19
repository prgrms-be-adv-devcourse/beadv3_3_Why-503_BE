package io.why503.performanceservice.domain.seat.service;

import java.util.List;

import io.why503.performanceservice.domain.concerthall.model.entity.ConcertHallEntity;
import io.why503.performanceservice.domain.seat.model.dto.cmd.SeatAreaCreateCmd;
import io.why503.performanceservice.domain.seat.model.entity.SeatEntity;

public interface SeatService {
    /**
     * 공연장 기준 좌석 목록 조회
     */
    List<SeatEntity> findByConcertHall(Long concertHallSq);
    
    /**
     * 관리자 입력 기반 커스텀 좌석 일괄 생성
     * - 구역별 좌석 수를 입력받아 생성
     */
    void createCustomSeats(
        ConcertHallEntity concertHall,
        List<SeatAreaCreateCmd> areaCreateCmds
    );

}
