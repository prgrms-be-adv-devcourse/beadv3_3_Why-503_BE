package io.why503.performanceservice.domain.seat.Sv;

import java.util.List;

import io.why503.performanceservice.domain.concerthall.Model.Ett.ConcertHallEtt;
import io.why503.performanceservice.domain.seat.Model.Dto.Cmd.SeatAreaCreateCmd;
import io.why503.performanceservice.domain.seat.Model.Ett.SeatEtt;

public interface SeatSv {
    /**
     * 공연장 기준 좌석 목록 조회
     */
    List<SeatEtt> findByConcertHall(Long concertHallSq);
    
    /**
     * 관리자 입력 기반 커스텀 좌석 일괄 생성
     * - 구역별 좌석 수를 입력받아 생성
     */
    void createCustomSeats(
        ConcertHallEtt concertHall,
        List<SeatAreaCreateCmd> areaCreateCmds
    );

}
