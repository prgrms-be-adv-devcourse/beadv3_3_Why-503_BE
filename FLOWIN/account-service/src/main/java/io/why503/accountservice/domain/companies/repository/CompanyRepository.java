/**
 * Company Repository
 * 사용 목적 :
 * - Company Entity에 대한 DB 접근 처리
 * - 회사 정보 저장 및 조회 기능 제공
 */
package io.why503.accountservice.domain.companies.repository;

import io.why503.accountservice.domain.companies.model.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    // JpaRepository 기본 CRUD 메서드 사용
    Optional<Company> findBySq(Long sq);
}
