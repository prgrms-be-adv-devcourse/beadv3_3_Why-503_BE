package io.why503.performanceservice.domain.roundSeat.repository;

import feign.Param;
import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.roundSeat.model.enums.RoundSeatStatus;
import io.why503.performanceservice.domain.roundSeat.model.entity.RoundSeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
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

    // Bulk Update + 낙관적 락 수동 적용
    // DB 쿼리 1방으로 처리 (성능 최적화)
    // version을 1 증가시켜 JPA 낙관적 락 메커니즘 유지
    @Modifying(clearAutomatically = true) // 쿼리 실행 후 영속성 컨텍스트 초기화 (필수)
    @Query("""
            UPDATE RoundSeatEntity r
            SET r.status = :newStatus,
                r.statusDt = :now,
                r.version = r.version + 1
            WHERE r.sq IN :ids
              AND r.status = :oldStatus
            """)
    int updateStatusBulk(@Param("ids") List<Long> ids,
                         @Param("newStatus") RoundSeatStatus newStatus,
                         @Param("oldStatus") RoundSeatStatus oldStatus,
                         @Param("now") LocalDateTime now);

    //선점된지 10분 지난 좌석을 일괄 해제
    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE RoundSeatEntity r
            SET r.status = :availableStatus,
                r.statusDt = :now,
                r.version = r.version + 1
            WHERE r.status = :reservedStatus
              AND r.statusDt <= :threshold
            """)
    int releaseExpiredSeats(
            @Param("threshold") LocalDateTime threshold,
            @Param("now") LocalDateTime now,
            @Param("availableStatus") RoundSeatStatus availableStatus,
            @Param("reservedStatus") RoundSeatStatus reservedStatus
    );
}
