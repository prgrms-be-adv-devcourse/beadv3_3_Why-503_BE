package io.why503.companyservice.Sv;

import io.why503.companyservice.Model.Dto.CompanyReqDto;
import io.why503.companyservice.Model.Dto.CompanyResDto;

public interface CompanySv {

    // 회사 등록 (사용자 식별자 기반)
    void registerCompany(CompanyReqDto requestDto, Long userSq);

    // 회사 식별자 기준 회사 정보 조회
    CompanyResDto getCompanyByCompanySq(Long companySq);

    // 회사 등록
    void registerCompany(CompanyReqDto requestDto);

}
