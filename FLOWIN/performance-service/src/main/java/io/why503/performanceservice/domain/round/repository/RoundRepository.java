package io.why503.performanceservice.domain.round.repository;

import io.why503.performanceservice.domain.round.model.enums.RoundStatus;
import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.show.model.entity.ShowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RoundRepository extends JpaRepository<RoundEntity,Long> {

    // 특정 공연에 걸려 있는 모든 회차를 다 가져올 때 사용
    List<RoundEntity> findByShow(ShowEntity showSq);

    //예매가 가능한 것만 골라낼 때 사용
    List<RoundEntity> findByShowAndStatus(ShowEntity showSq, RoundStatus roundStatus);

    // 특정 공연의 특정 날짜 구간에 있는 모든 회차를 조회 (재정렬용)
    List<RoundEntity> findAllByShowAndStartDtBetween(ShowEntity showSq, LocalDateTime start, LocalDateTime end);

    //특정 공연이 해당 시간에 이미 존재하는지 확인
    boolean existsByShowAndStartDt(ShowEntity showSq, LocalDateTime roundDt);
}


