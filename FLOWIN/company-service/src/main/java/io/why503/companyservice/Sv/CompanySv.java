package io.why503.companyservice.Sv;

import io.why503.companyservice.Model.Dto.CompanyReqDto;
import io.why503.companyservice.Model.Dto.CompanyResDto;

public interface CompanySv {
    void registerCompany(CompanyReqDto requestDto);

    CompanyResDto getCompanyByCompanySq(Long companySq);
    // CompanyReqDto getCompanyByUserDto(Long userSq);

    // CompanyResDto getCompanyByUserSq(Long userSq);

}
