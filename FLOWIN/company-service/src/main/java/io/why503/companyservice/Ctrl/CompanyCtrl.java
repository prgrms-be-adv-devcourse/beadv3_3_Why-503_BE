/**
 * Company Controller
 * 회사 등록 및 조회 요청을 처리하는 API 컨트롤러
 *
 * 사용 목적 :
 * - 기업 회원의 회사 정보 등록
 * - 회사 식별자 기준 회사 정보 조회
 */
package io.why503.companyservice.Ctrl;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.why503.companyservice.Model.Dto.CompanyReqDto;
import io.why503.companyservice.Model.Dto.CompanyResDto;
import io.why503.companyservice.Sv.CompanySv;

@RestController
@RequiredArgsConstructor // Service 의존성 생성자 주입
@RequestMapping("/company")
public class CompanyCtrl {

    private final CompanySv companyService; // 회사 비즈니스 로직 처리 Service

    @PostMapping // 회사 등록 API
    public ResponseEntity<Void> registerCompany(
            @RequestBody CompanyReqDto requestDto // 회사 등록에 필요한 요청 데이터
    ) {
        // 회사 등록 비즈니스 로직
        companyService.registerCompany(requestDto);

        return ResponseEntity.ok().build(); // 등록 성공 시 200 OK 반환
    }

    @GetMapping("/{companySq}") // 회사 조회 API
    public ResponseEntity<CompanyResDto> getCompany(
            @PathVariable("companySq") Long companySq // 조회할 회사 식별자
    ) {
        return ResponseEntity.ok(
                companyService.getCompanyByCompanySq(companySq) // 회사 정보 조회 결과 반환
        );
    }
}
