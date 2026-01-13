/**
 * Show Repository
 * 공연(Show) 엔티티에 대한 DB 접근을 담당하는 JPA Repository
 *
 * 사용 목적 :
 * - 공연 정보 저장
 * - 공연 단건 / 목록 조회
 *
 * 특징 :
 * - Spring Data JPA 기본 CRUD 기능 사용
 * - 복잡한 쿼리는 추후 Custom Repository로 확장 가능
 */
package io.why503.performanceservice.domain.show.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.why503.performanceservice.domain.show.Model.Ett.ShowEtt;

@Repository
public interface ShowRepo extends JpaRepository<ShowEtt, Long> {
}
