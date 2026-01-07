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

    private final CompanyRepo companyRepository;

    @Override
    public void registerCompany(CompanyReqDto requestDto) {

        // // 회원당 회사 1개 제한
        // if (companyRepository.existsByUserSq(requestDto.getUserSq())) {
        //     throw new IllegalStateException("이미 등록된 회사가 있습니다.");
        // }

        Company company = Company.create(
                // requestDto.getUserSq(),
                requestDto.getCompanyBank(),
                requestDto.getAccount(),
                requestDto.getCompanyName(),
                requestDto.getOwnerName(),
                requestDto.getCompanyPhone(),
                requestDto.getCompanyEmail(),
                requestDto.getCompanyAddr(),
                requestDto.getCompanyPost(),
                requestDto.getAmount()
        );

        companyRepository.save(company);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResDto getCompanyByCompanySq(Long companySq) {

        Company company = companyRepository.findById(companySq)
                .orElseThrow(() -> new IllegalArgumentException("회사 정보를 찾을 수 없습니다."));

        return new CompanyResDto(company);
    }

    // @Override
    // @Transactional(readOnly = true)
    // public CompanyResDto getCompanyByUserSq(Long userSq) {

    //     Company company = companyRepository.findByUserSq(userSq)
    //             .orElseThrow(() -> new IllegalArgumentException("회사 정보가 존재하지 않습니다."));

    //     return new CompanyResDto(company);
    // }

    // @Override
    // public CompanyReqDto getCompanyByUserDto(Long userSq) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'getCompanyByUserDto'");
    // }
}
