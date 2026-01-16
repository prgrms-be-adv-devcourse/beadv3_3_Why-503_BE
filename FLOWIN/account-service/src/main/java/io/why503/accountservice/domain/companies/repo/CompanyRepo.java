/**
 * Company Repository
 * 사용 목적 :
 * - Company Entity에 대한 DB 접근 처리
 * - 회사 정보 저장 및 조회 기능 제공
 */
package io.why503.accountservice.domain.companies.repo;

import io.why503.accountservice.domain.companies.model.ett.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepo extends JpaRepository<Company, Long> {
    // JpaRepository 기본 CRUD 메서드 사용
}
