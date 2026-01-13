/**
 * Concert Hall Repository
 * 공연장 엔티티에 대한 DB 접근을 담당하는 Repository
 *
 * 사용 목적 :
 * - 공연장 등록, 조회 등 기본 CRUD 처리
 * - JPA 기반 데이터 접근 계층 분리
 *
 * 설계 메모 :
 * - 현재는 기본 JpaRepository 기능만 사용
 * - 추후 공연장 상태별 조회, 위치 기반 조회 등 커스텀 쿼리 확장 가능
 * ex)
 * Optional<ConcertHallEtt> findByConcertHallName(String name);
 * List<ConcertHallEtt> findByConcertHallStat(String stat);
 */
package io.why503.performanceservice.domain.concerthall.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.why503.performanceservice.domain.concerthall.Model.Ett.ConcertHallEtt;

@Repository
public interface ConcertHallRepo extends JpaRepository<ConcertHallEtt, Long> {
}
