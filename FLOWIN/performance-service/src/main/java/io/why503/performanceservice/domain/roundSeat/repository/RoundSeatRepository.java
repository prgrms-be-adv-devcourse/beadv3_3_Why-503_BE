package io.why503.performanceservice.domain.roundSeat.repository;

import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.roundSeat.model.enums.RoundSeatStatus;
import io.why503.performanceservice.domain.roundSeat.model.entity.RoundSeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoundSeatRepository extends JpaRepository<RoundSeatEntity,Long> {
    // 변수명 roundSq가 객체이므로, 그 안의 ID를 찾기 위해 _RoundSq 추가
    //특정 회차의 모든 좌석 조회
    List<RoundSeatEntity> findByRound_Sq(Long roundSq);

    //특정 회차의 특정 상태인 좌석만 조회
    List<RoundSeatEntity> findByRound_SqAndStatus(Long roundSq, RoundSeatStatus roundSeatStatus);

    //특정 회차에 특정좌석SQ를 자진 데이터가 존재하는지 확인
    boolean existsByRoundAndShowSeatSq(RoundEntity round, Long showSeatSq);

    //좌석의 상태를 기준으로 좌석 리스트를 가져옴
    List<RoundSeatEntity> findAllByStatus(RoundSeatStatus status);
}
