package io.why503.accountservice.util;


import io.why503.accountservice.domain.company.model.dto.cmd.CompanyCmd;
import io.why503.accountservice.domain.company.model.dto.req.CompanyReqDto;
import io.why503.accountservice.domain.company.model.dto.res.CompanyResDto;
import io.why503.accountservice.domain.company.model.ett.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/*
여러 dto를 다른 dto로 만들어주는 스위치역할
추가할 가능성 있음
 */
@Component
@RequiredArgsConstructor
public class CompanyMapper {
    public CompanyCmd ReqDtoToCmd(CompanyReqDto request){
        return CompanyCmd.builder()
                .companyBank(request.getCompanyBank())      .accountNumber(request.getAccountNumber())
                .companyName(request.getCompanyName())      .ownerName(request.getOwnerName())
                .companyPhone(request.getCompanyPhone())    .companyEmail(request.getCompanyEmail())
                .companyAddr(request.getCompanyAddr())      .companyPost(request.getCompanyPost())
                .build();
    }
    public CompanyResDto EttToResDto(Company company){
        return CompanyResDto.builder()
                .companySq(company.getCompanySq())          .companyBank(company.getCompanyBank())
                .accountNumber(company.getAccountNumber())  .companyName(company.getCompanyName())
                .ownerName(company.getOwnerName())          .companyPhone(company.getCompanyPhone())
                .companyEmail(company.getCompanyEmail())    .companyAddr(company.getCompanyAddr())
                .companyPost(company.getCompanyPost())      .amount(company.getAmount())
                .amountDate(company.getAmountDate())
                .build();
    }
}
