/**
 * Company Controller
 * 회사 등록 및 조회 요청을 처리하는 API 컨트롤러
 * 사용 목적 :
 * - 기업 회원의 회사 정보 등록
 * - 회사 식별자 기준 회사 정보 조회
 */
package io.why503.accountservice.domain.companies.controller;

import io.why503.accountservice.domain.accounts.model.dto.response.UserRoleResponse;
import io.why503.accountservice.domain.accounts.model.enums.UserRole;
import io.why503.accountservice.domain.accounts.service.AccountService;
import io.why503.accountservice.domain.companies.model.dto.requset.CompanyRequest;
import io.why503.accountservice.domain.companies.model.dto.response.CompanySummaryResponse;
import io.why503.accountservice.domain.companies.service.CompanyService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor // Service 의존성 생성자 주입
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService; // 회사 비즈니스 로직 처리 Service
    private final AccountService accountService;
    @PostMapping // 회사 등록 API
    public ResponseEntity<Void> registerCompany(
            @RequestBody CompanyRequest request, // 회사 등록에 필요한 요청 데이터
            @RequestHeader("X-USER-SQ") Long userSq
    ) {
        //권한이 COMPANY가 아니면 거부
        if(accountService.readUserRoleBySq(userSq) != UserRole.COMPANY){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // 회사 등록 비즈니스 로직
        companyService.registerCompany(userSq, request);

        return ResponseEntity.ok().build(); // 등록 성공 시 200 OK 반환
    }

    @GetMapping("/{companySq}") // 회사 조회 API
    public ResponseEntity<CompanySummaryResponse> getCompany(
            @PathVariable Long companySq // 조회할 회사 식별자
    ) {
        return ResponseEntity.ok(
                companyService.getCompanyBySq(companySq) // 회사 정보 조회 결과 반환
        );
    }
    @GetMapping("/member/{companySq}") // 회사 조회 API
    public ResponseEntity<List<UserRoleResponse>> getCompanyMembers(
            @PathVariable Long companySq // 조회할 회사 식별자
    ) {
        return ResponseEntity.ok(
                accountService.readCompanyMember(companySq) //회사 맨버 전체 조회
        );
    }
}
