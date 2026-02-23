/**
 * Company Service Implementation
 * 사용 목적 :
 * - 회사 등록 및 조회에 대한 실제 비즈니스 로직 구현
 * - Repository를 통해 Company Entity 영속화 및 조회 처리
 */
package io.why503.accountservice.domain.companies.service.impl;

import io.why503.accountbase.model.enums.UserRole;
import io.why503.accountservice.domain.accounts.service.AccountService;
import io.why503.accountservice.domain.companies.model.dto.requset.CompanyRequest;
import io.why503.accountservice.domain.companies.model.dto.response.CompanySettlementResponse;
import io.why503.accountservice.domain.companies.model.dto.response.CompanySummaryResponse;
import io.why503.accountservice.domain.companies.model.entity.Company;
import io.why503.accountservice.domain.companies.repository.CompanyRepository;
import io.why503.accountservice.domain.companies.service.CompanyService;
import io.why503.accountservice.domain.companies.util.CompanyExceptionFactory;
import io.why503.accountservice.domain.companies.util.CompanyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyServiceImpl implements CompanyService {

    private final AccountService accountService;
    private final CompanyRepository companyRepository;// 회사 Entity DB 접근 Repository
    private final CompanyMapper companyMapper;

    //내부 중복 간소화 메소드
    private Company findBySq(Long sq){
        return companyRepository.findBySq(sq)
                .orElseThrow(() -> CompanyExceptionFactory.companyNotFound(sq + " company is not found")
                ); // 회사 미존재 시 예외 처리
    }
    /*
    생성할 때 생성한 유저의 외래키에 company삽입
     */
    @Override
    @Transactional
    public void registerCompany(Long userSq, CompanyRequest request) {
        Company company = companyMapper.requestToEntity(request);// 회사 Entity 생성
        companyRepository.save(company);// 회사 정보 DB 저장
        accountService.joinCompany(userSq, company, UserRole.COMPANY); //회사 주입
    }

    @Override // 조회 전용 트랜잭션
    public CompanySummaryResponse getCompanyBySq(Long sq) {
        Company company = findBySq(sq);
        return companyMapper.entityToSummaryResponse(company); // Entity → Response DTO 변환 후 반환
    }
    //엔티티 반환, 내부 통신용
    @Override
    public Company readCompanyBySq(Long sq) {
        return findBySq(sq);
    }

    @Override
    public CompanySettlementResponse getCompanySettlementInfo(Long sq) {
        // 이미 구현되어 있는 내부 메서드를 재사용하여 엔티티 조회 (예외 처리 포함됨)
        Company company = findBySq(sq);

        // Entity -> DTO 직접 변환 후 반환
        return new CompanySettlementResponse(
                sq,
                company.getBank().name(),
                company.getAccountNumber(),
                company.getOwnerName()
        );
    }

}
