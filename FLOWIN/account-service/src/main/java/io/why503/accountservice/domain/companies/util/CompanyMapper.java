package io.why503.accountservice.domain.companies.util;

import io.why503.accountservice.domain.companies.model.dto.requset.CompanyRequest;
import io.why503.accountservice.domain.companies.model.dto.response.CompanySummaryResponse;
import io.why503.accountservice.domain.companies.model.entity.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/*
여러 dto를 다른 dto로 만들어주는 스위치역할
추가할 가능성 있음
 */
@Component
@RequiredArgsConstructor
public class CompanyMapper {
    public Company RequestToEntity(CompanyRequest request){
        return Company.builder()
                .bank(request.companyBank())
                .accountNumber(request.accountNumber())
                .name(request.companyName())
                .ownerName(request.ownerName())
                .phone(request.companyPhone())
                .email(request.companyEmail())
                .basicAddr(request.companyBasicAddr())
                .detailAddr(request.companyDetailAddr())
                .post(request.companyPost())
                .amountDate(request.amountDate())
                .build();
    }
    public CompanySummaryResponse EntityToSummaryResponse(Company company){
        return new CompanySummaryResponse(
                company.getSq(),
                company.getName(),
                company.getOwnerName(),
                company.getPhone(),
                company.getEmail(),
                company.getBasicAddr(),
                company.getDetailAddr(),
                company.getPost()
        );
    }
}
