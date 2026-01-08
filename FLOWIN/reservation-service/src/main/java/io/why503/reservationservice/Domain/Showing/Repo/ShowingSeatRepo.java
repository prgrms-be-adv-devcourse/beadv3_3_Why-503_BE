package io.why503.reservationservice.Domain.Showing.Repo;

import io.why503.reservationservice.Domain.Showing.Model.Dto.AreaStatusDto;
import io.why503.reservationservice.Domain.Showing.Model.Dto.SeatStatusDto;
import io.why503.reservationservice.Domain.Showing.Model.Ett.ShowingSeat; // ★ 패키지 수정됨
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShowingSeatRepo extends JpaRepository<ShowingSeat, Long> {

    // 1. [직접 선택] 동시성 제어를 위한 비관적 락(Pessimistic Lock) 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ShowingSeat s WHERE s.showingSeatSq = :id")
    Optional<ShowingSeat> findByIdWithLock(@Param("id") Long id);

    // 2. [빠른 선택] 특정 회차(showingSq) + 등급(grade) + 빈 좌석(0) 목록 조회
    @Query("SELECT ss FROM ShowingSeat ss " +
            "JOIN ss.showSeat s " +
            "JOIN s.seatClass sc " +
            "WHERE ss.showingSq = :showingSq " +
            "AND ss.showingSeatStat = 0 " +
            "AND sc.seatClass = :grade")
    List<ShowingSeat> findAvailableSeats(@Param("showingSq") Long showingSq,
                                         @Param("grade") String grade);

    @Query("SELECT new io.why503.reservationservice.Domain.Showing.Model.Dto.AreaStatusDto(" +
            "s.seatArea, sc.seatClass, sc.seatPrice, COUNT(ss)) " +
            "FROM ShowingSeat ss " +
            "JOIN ss.showSeat ss_join " +
            "JOIN ss_join.seat s " +
            "JOIN ss_join.seatClass sc " +
            "WHERE ss.showingSq = :showingSq AND ss.showingSeatStat = 0 " +
            "GROUP BY s.seatArea, sc.seatClass, sc.seatPrice")
    List<AreaStatusDto> findAreaStatus(@Param("showingSq") Long showingSq);

    @Query("SELECT new io.why503.reservationservice.Domain.Showing.Model.Dto.SeatStatusDto(" +
            "ss.showingSeatSq, s.seatArea, s.areaSeatNo, ss.showingSeatStat) " +
            "FROM ShowingSeat ss " +
            "JOIN ss.showSeat ssj " +
            "JOIN ssj.seat s " +
            "WHERE ss.showingSq = :showingSq")
    List<SeatStatusDto> findAllSeatStatusDtoByShowingSq(@Param("showingSq") Long showingSq);

    @Query("SELECT new io.why503.reservationservice.Domain.Showing.Model.Dto.SeatStatusDto(" +
            "ss.showingSeatSq, s.seatArea, s.areaSeatNo, ss.showingSeatStat) " +
            "FROM ShowingSeat ss " +
            "JOIN ss.showSeat ssj " +
            "JOIN ssj.seat s " +
            "WHERE ss.showingSq = :showingSq " +
            "AND s.seatArea = :area")
    List<SeatStatusDto> findSeatStatusByArea(
            @Param("showingSq") Long showingSq,
            @Param("area") String area
    );
}