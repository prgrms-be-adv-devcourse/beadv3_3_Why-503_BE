/**
 * Company Repository
 *
 * 사용 목적 :
 * - Company Entity에 대한 DB 접근 처리
 * - 회사 정보 저장 및 조회 기능 제공
 */
package io.why503.companyservice.Repo;

import io.why503.companyservice.Model.Ett.Company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Company Entity 전용 JPA Repository
public interface CompanyRepo extends JpaRepository<Company, Long> {
    // JpaRepository 기본 CRUD 메서드 사용
}
