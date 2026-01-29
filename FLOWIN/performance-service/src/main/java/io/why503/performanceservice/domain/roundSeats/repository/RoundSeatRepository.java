package io.why503.performanceservice.domain.roundSeats.repository;

import io.why503.performanceservice.domain.roundSeats.model.enums.RoundSeatStatus;
import io.why503.performanceservice.domain.roundSeats.model.entity.RoundSeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoundSeatRepository extends JpaRepository<RoundSeatEntity,Long> {
    // 변수명 roundSq가 객체이므로, 그 안의 ID를 찾기 위해 _RoundSq 추가
    //특정 회차의 모든 좌석 조회
    List<RoundSeatEntity> findByRound_Sq(Long roundSq);

    //특정 회차의 특정 상태인 좌석만 조회
    List<RoundSeatEntity> findByRound_SqAndStatus(Long roundSq, RoundSeatStatus roundSeatStatus);

}
