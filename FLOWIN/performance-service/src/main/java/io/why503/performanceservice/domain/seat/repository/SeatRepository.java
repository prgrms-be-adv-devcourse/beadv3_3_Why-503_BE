package io.why503.performanceservice.domain.seat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.why503.performanceservice.domain.seat.model.entity.SeatEntity;

import java.util.List;

public interface SeatRepository extends JpaRepository<SeatEntity, Long> {

    // 공연장 기준 좌석 전체 조회

    List<SeatEntity> findAllByHall_SqOrderByAreaAscNumInAreaAsc(
            Long hallSq
    );

    // 공연장 + 구역 + 구역 내 좌석번호 기준 좌석 존재 여부 확인
    boolean existsByHallSqAndAreaAndNumInArea(
            Long hallSq,
            String seatArea,
            Integer numInArea
    );

    // 공연장 기준 좌석 개수 조회
    long countByHallSq(Long hallSq);
    
    List<SeatEntity> findByHall_SqAndArea(
        Long hallSq,
        String seatArea
        );

}
