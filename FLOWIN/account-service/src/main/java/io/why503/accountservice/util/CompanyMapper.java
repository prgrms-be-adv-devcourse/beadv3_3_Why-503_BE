package io.why503.accountservice.util;


import io.why503.accountservice.domain.companies.model.dto.vo.CompanyVo;
import io.why503.accountservice.domain.companies.model.dto.requset.CompanyRequest;
import io.why503.accountservice.domain.companies.model.dto.response.CompanyResponse;
import io.why503.accountservice.domain.companies.model.entitys.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/*
여러 dto를 다른 dto로 만들어주는 스위치역할
추가할 가능성 있음
 */
@Component
@RequiredArgsConstructor
public class CompanyMapper {
    public CompanyVo ReqDtoToCmd(CompanyRequest request){
        return new CompanyVo(
                request.companyBank(),
                request.accountNumber(),
                request.companyName(),
                request.ownerName(),
                request.companyPhone(),
                request.companyEmail(),
                request.companyBasicAddr(),
                request.companyDetailAddr(),
                request.companyPost()
        );
    }
    public CompanyResponse EttToResDto(Company company){
        return new CompanyResponse(
                company.getSq(),
                company.getBank(),
                company.getAccountNumber(),
                company.getName(),
                company.getOwnerName(),
                company.getPhone(),
                company.getEmail(),
                company.getBasicAddr(),
                company.getDetailAddr(),
                company.getPost(),
                company.getAmount(),
                company.getAmountDate()
        );
    }
}
