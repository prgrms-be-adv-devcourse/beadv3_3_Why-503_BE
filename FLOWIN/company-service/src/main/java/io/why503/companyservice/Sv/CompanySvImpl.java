/**
 * Company Service Implementation
 *
 * 사용 목적 :
 * - 회사 등록 및 조회에 대한 실제 비즈니스 로직 구현
 * - Repository를 통해 Company Entity 영속화 및 조회 처리
 */
package io.why503.companyservice.Sv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.why503.companyservice.Model.Dto.CompanyReqDto;
import io.why503.companyservice.Model.Dto.CompanyResDto;
import io.why503.companyservice.Model.Ett.Company;
import io.why503.companyservice.Repo.CompanyRepo;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanySvImpl implements CompanySv {

    private final CompanyRepo companyRepository; // 회사 Entity DB 접근 Repository

    @Override
    public void registerCompany(CompanyReqDto requestDto) {

        // 회사 Entity 생성
        Company company = Company.builder()
            .companyBank(requestDto.getCompanyBank())
            .account(requestDto.getAccount())
            .companyName(requestDto.getCompanyName())
            .ownerName(requestDto.getOwnerName())
            .companyPhone(requestDto.getCompanyPhone())
            .companyEmail(requestDto.getCompanyEmail())
            .companyAddr(requestDto.getCompanyAddr())
            .companyPost(requestDto.getCompanyPost())
            .amount(requestDto.getAmount())
            .build();

        companyRepository.save(company); // 회사 정보 DB 저장
    }

    @Override
    @Transactional(readOnly = true) // 조회 전용 트랜잭션
    public CompanyResDto getCompanyByCompanySq(Long companySq) {

        Company company = companyRepository.findById(companySq)
                .orElseThrow(() -> new IllegalArgumentException("회사 정보를 찾을 수 없습니다.")); // 회사 미존재 시 예외 처리

        return new CompanyResDto(company); // Entity → Response DTO 변환 후 반환
    }

    @Override
    public void registerCompany(CompanyReqDto requestDto, Long userSq) {
        // 사용자-회사 연동 등록 기능 (추후 구현 예정)
        throw new UnsupportedOperationException("Unimplemented method 'registerCompany'");
    }
}
