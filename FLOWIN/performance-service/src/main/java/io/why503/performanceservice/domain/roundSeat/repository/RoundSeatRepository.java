package io.why503.performanceservice.domain.roundSeat.repository;

import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.roundSeat.model.entity.RoundSeatEntity;
import io.why503.performanceservice.domain.roundSeat.model.enums.RoundSeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface RoundSeatRepository extends JpaRepository<RoundSeatEntity, Long> {

    // 특정 회차의 모든 좌석 조회
    List<RoundSeatEntity> findByRound_Sq(Long roundSq);

    // 특정 회차의 특정 상태인 좌석만 조회
    List<RoundSeatEntity> findByRound_SqAndStatus(Long roundSq, RoundSeatStatus roundSeatStatus);

    // 특정 회차에 특정좌석SQ를 가진 데이터가 존재하는지 확인
    boolean existsByRoundAndShowSeatSq(RoundEntity round, Long showSeatSq);

    // 좌석의 상태를 기준으로 좌석 리스트를 가져옴
    List<RoundSeatEntity> findAllByStatus(RoundSeatStatus status);

    // Bulk Update + 낙관적 락 수동 적용
    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE RoundSeatEntity r
            SET r.status = :newStatus,
                r.statusDt = :now,
                r.version = r.version + 1
            WHERE r.sq IN :ids
              AND r.status = :oldStatus
            """)
    int updateStatusBulk(
            @Param("ids") List<Long> ids,
            @Param("newStatus") RoundSeatStatus newStatus,
            @Param("oldStatus") RoundSeatStatus oldStatus,
            @Param("now") LocalDateTime now
    );

    // 선점된지 10분 지난 좌석을 일괄 해제
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
    // WAIT인 좌석만 AVAILABLE로 변경
    @Modifying(clearAutomatically = true)
        @Query("""
        UPDATE RoundSeatEntity r
        SET r.status = :newStatus,
                r.statusDt = :now,
                r.version = r.version + 1
        WHERE r.round.sq = :roundSq
        AND r.status = :oldStatus
        """)
        int updateStatusByRoundSqAndOldStatus(
                @Param("roundSq") Long roundSq,
                @Param("newStatus") RoundSeatStatus newStatus,
                @Param("oldStatus") RoundSeatStatus oldStatus,
                @Param("now") LocalDateTime now
        );

    // 정산용: 특정 공연(showSq)에 속한 모든 회차 좌석(roundSeatSq)의 식별자 목록 조회
    @Query("SELECT rs.sq FROM RoundSeatEntity rs JOIN rs.round r WHERE r.show.sq = :showSq")
    List<Long> findRoundSeatSqsByShowSq(@Param("showSq") Long showSq);
}