package io.why503.performanceservice.domain.seat.repo;

import io.why503.performanceservice.domain.seat.model.Ett.SeatEtt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface SeatRepo extends JpaRepository<SeatEtt, Long> {

    // 공연장 전체 좌석 조회
    List<SeatEtt> findByConcertHallId(Long concertHallEtt);

    // 공연장 + 구역
    List<SeatEtt> findByConcertHallIdAndSeatArea(
            Long concertHallId,
            String seatArea
    );

    // 공연장 + 구역 + 좌석번호
    List<SeatEtt> findByConcertHallIdAndSeatAreaAndSeatNo(
            Long concertHallId,
            String seatArea,
            int seatNo
    );


}
