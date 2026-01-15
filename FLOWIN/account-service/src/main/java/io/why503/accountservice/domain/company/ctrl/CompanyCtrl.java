/**
 * Company Controller
 * 회사 등록 및 조회 요청을 처리하는 API 컨트롤러
 * 사용 목적 :
 * - 기업 회원의 회사 정보 등록
 * - 회사 식별자 기준 회사 정보 조회
 */
package io.why503.accountservice.domain.company.ctrl;

import io.why503.accountservice.domain.account.model.dto.UserRole;
import io.why503.accountservice.domain.account.sv.AccountSv;
import io.why503.accountservice.domain.company.model.dto.req.CompanyReqDto;
import io.why503.accountservice.domain.company.model.dto.res.CompanyResDto;
import io.why503.accountservice.domain.company.sv.CompanySv;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor // Service 의존성 생성자 주입
@RequestMapping("/company")
public class CompanyCtrl {

    private final CompanySv companyService; // 회사 비즈니스 로직 처리 Service
    private final AccountSv accountSv;
    @PostMapping // 회사 등록 API
    public ResponseEntity<Void> registerCompany(
            @RequestBody CompanyReqDto requestDto, // 회사 등록에 필요한 요청 데이터
            @RequestHeader("X-USER-SQ") Long sq
    ) {
        //권한이 COMPANY가 아니면 거부
        if(accountSv.readUserRoleBySq(sq) != UserRole.COMPANY){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // 회사 등록 비즈니스 로직
        companyService.registerCompany(sq, requestDto);

        return ResponseEntity.ok().build(); // 등록 성공 시 200 OK 반환
    }

    @GetMapping("/{companySq}") // 회사 조회 API
    public ResponseEntity<CompanyResDto> getCompany(
            @PathVariable Long companySq // 조회할 회사 식별자
    ) {
        return ResponseEntity.ok(
                companyService.getCompanyByCompanySq(companySq) // 회사 정보 조회 결과 반환
        );
    }
}
