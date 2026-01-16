package io.why503.performanceservice.domain.roundSeats.repository;

import io.why503.performanceservice.domain.roundSeats.model.dto.RoundSeatStatus;
import io.why503.performanceservice.domain.roundSeats.model.entity.RoundSeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoundSeatRepository extends JpaRepository<RoundSeatEntity,Long> {

    //특정 회차의 모든 좌석 조회
    List<RoundSeatEntity> findByRoundSq(Long roundSq);

    //특정 회차의 특정 상태인 좌석만 조회
    List<RoundSeatEntity> findByRoundSqAndRoundSeatStatus(Long roundSq, RoundSeatStatus roundSeatStatus);

}
