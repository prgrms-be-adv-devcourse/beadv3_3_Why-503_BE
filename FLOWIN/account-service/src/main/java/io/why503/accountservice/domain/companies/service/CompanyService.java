package io.why503.accountservice.domain.companies.service;

import io.why503.accountservice.domain.companies.model.dto.requset.CompanyRequest;
import io.why503.accountservice.domain.companies.model.dto.response.CompanySummaryResponse;
import io.why503.accountservice.domain.companies.model.entity.Company;

public interface CompanyService {

    // 회사 등록 (사용자 식별자 기반)
    void registerCompany(Long userSq, CompanyRequest request);

    // 회사 식별자 기준 회사 정보 조회
    CompanySummaryResponse getCompanyBySq(Long sq);
    // sq로 회사 엔티티 반환(서비스 내부 소통용)
    Company readCompanyBySq(Long sq);

}
