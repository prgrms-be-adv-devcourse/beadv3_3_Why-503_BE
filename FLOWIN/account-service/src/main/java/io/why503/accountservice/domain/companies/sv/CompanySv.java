package io.why503.accountservice.domain.companies.sv;

import io.why503.accountservice.domain.companies.model.dto.req.CompanyReqDto;
import io.why503.accountservice.domain.companies.model.dto.res.CompanyResDto;

public interface CompanySv {

    // 회사 등록 (사용자 식별자 기반)
    void registerCompany(Long userSq, CompanyReqDto requestDto);

    // 회사 식별자 기준 회사 정보 조회
    CompanyResDto getCompanyByCompanySq(Long companySq);


}
