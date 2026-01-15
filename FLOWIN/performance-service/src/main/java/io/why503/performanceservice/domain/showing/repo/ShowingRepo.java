package io.why503.performanceservice.domain.showing.repo;

import io.why503.performanceservice.domain.showing.model.dto.ShowingStat;
import io.why503.performanceservice.domain.showing.model.ett.ShowingEtt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowingRepo extends JpaRepository<ShowingEtt,Long> {

    // 특정 공연에 걸려 있는 모든 회차를 다 가져올 때 사용
    List<ShowingEtt> findByShow(Long showSq);

    //예매가 가능한 것만 골라낼 때 사용
    List<ShowingEtt> findByShowAndStat(Long showSq, ShowingStat stat);

    //회차 번호 증가에 사용
    Integer countByShowAndDtBetween(Long showSq, LocalDateTime start, LocalDateTime end);

    //특정 공연이 해당 시간에 이미 존재하는지 확인
    boolean existsByShowAndDt(Long showSq, LocalDateTime dt);
}


