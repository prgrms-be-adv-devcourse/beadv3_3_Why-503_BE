/**
 * Company Service Implementation
 * 사용 목적 :
 * - 회사 등록 및 조회에 대한 실제 비즈니스 로직 구현
 * - Repository를 통해 Company Entity 영속화 및 조회 처리
 */
package io.why503.accountservice.domain.companies.service.impl;

import io.why503.accountservice.domain.accounts.model.entity.Account;
import io.why503.accountservice.domain.companies.model.dto.requset.CompanyRequest;
import io.why503.accountservice.domain.companies.model.dto.response.CompanyResponse;
import io.why503.accountservice.domain.companies.model.entitys.Company;
import io.why503.accountservice.domain.companies.repository.CompanyRepository;
import io.why503.accountservice.domain.companies.service.CompanyService;
import io.why503.accountservice.util.CompanyMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;// 회사 Entity DB 접근 Repository
    private final CompanyMapper companyMapper;
    private final EntityManager em;

    /*
    생성할 때 무조건 어차피 개인이 생성하니까 그대로 개인과 연결
     */
    @Override
    public void registerCompany(Long userSq, CompanyRequest requestDto) {
        // 회사 Entity 생성
        Company company = new Company(em.getReference(Account.class, userSq), companyMapper.ReqDtoToCmd(requestDto));
        companyRepository.save(company);// 회사 정보 DB 저장
    }

    @Override
    @Transactional(readOnly = true) // 조회 전용 트랜잭션
    public CompanyResponse getCompanyByCompanySq(Long companySq) {

        Company company = companyRepository.findById(companySq)
                .orElseThrow(() -> new IllegalArgumentException("회사 정보를 찾을 수 없습니다.")); // 회사 미존재 시 예외 처리

        return companyMapper.EttToResDto(company); // Entity → Response DTO 변환 후 반환
    }

}
