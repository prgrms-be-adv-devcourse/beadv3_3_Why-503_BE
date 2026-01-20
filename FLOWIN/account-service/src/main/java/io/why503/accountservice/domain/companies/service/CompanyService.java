package io.why503.accountservice.domain.companies.service;

import io.why503.accountservice.domain.companies.model.dto.requset.CompanyRequest;
import io.why503.accountservice.domain.companies.model.dto.response.CompanyResponse;

public interface CompanyService {

    // 회사 등록 (사용자 식별자 기반)
    void registerCompany(Long userSq, CompanyRequest request);

    // 회사 식별자 기준 회사 정보 조회
    CompanyResponse getCompanyByCompanySq(Long companySq);


}
