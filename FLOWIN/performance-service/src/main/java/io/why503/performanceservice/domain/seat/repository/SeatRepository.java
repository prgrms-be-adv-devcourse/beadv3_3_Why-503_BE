/**
 * SeatRepo (Repository)
 * 목적:
 * - 공연장 기준 좌석 조회/존재 여부 확인을 위한 DB 접근 계층
 * 필요한 기능:
 * 1) 공연장 기준 전체 좌석 목록 조회
 *    - concertHallSq로 검색
 *    - 정렬: seat_area ASC, area_seat_no ASC (사람이 보기 좋게)
 * 2) 공연장 + (seat_area, area_seat_no) 기준 좌석 존재 여부 확인
 *    - 중복 생성 방지 로직에서 사용 가능 (Unique 제약 보조)
 * 3) (선택) 공연장 기준 좌석 개수 조회
 *    - 초기 생성 완료 검증 / 관리용
 * 반환 정책:
 * - 목록: List<SeatEtt>
 * - 단건: Optional<SeatEtt>
 * - 존재: boolean
 */

package io.why503.performanceservice.domain.seat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.why503.performanceservice.domain.seat.model.entity.SeatEntity;

import java.util.List;

public interface SeatRepository extends JpaRepository<SeatEntity, Long> {

    /**
     * 공연장 기준 좌석 전체 조회
     * - 정렬 기준: 구역(seat_area) → 구역 내 좌석번호(area_seat_no)
     */
    List<SeatEntity> findAllByHallSqOrderByAreaAscNumInAreaAsc(
            Long hallSq
    );

    /**
     * 공연장 + 구역 + 구역 내 좌석번호 기준 좌석 존재 여부 확인
     * - 좌석 중복 생성 방지 보조용
     */
    boolean existsByHallSqAndAreaAndNumInArea(
            Long hallSq,
            String seatArea,
            Integer numInArea
    );

    /**
     * 공연장 기준 좌석 개수 조회
     * - 좌석 초기 생성 여부 검증 / 관리용
     */
    long countByHallSq(Long hallSq);
    
    List<SeatEntity> findByHall_SqAndArea(
        Long hallSq,
        String seatArea
        );

}
