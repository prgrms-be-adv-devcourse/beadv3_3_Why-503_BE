/**
 * Company Email Verify Controller
 * 회사 이메일 인증 코드 검증을 처리하는 API 컨트롤러
 * 사용 목적 :
 * - 사용자가 입력한 인증 코드 검증
 * - 인증 성공/실패 여부 반환
 */
package io.why503.accountservice.domain.auth.controller;

import io.why503.accountservice.domain.accounts.model.enums.UserRole;
import io.why503.accountservice.domain.accounts.service.AccountService;
import io.why503.accountservice.domain.auth.model.request.CompanyEmailRequest;
import io.why503.accountservice.domain.auth.model.request.CompanyVerifyRequest;
import io.why503.accountservice.domain.auth.model.response.CompanyEmailResponse;
import io.why503.accountservice.domain.auth.model.response.CompanyVerifyResponse;
import io.why503.accountservice.domain.auth.service.impl.CompanyAuthServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth/company")
@RequiredArgsConstructor
public class CompanyAuthController {
 // 이메일 인증 코드 검증 비즈니스 로직 처리 Service
    private final AccountService accountService;
    private final CompanyAuthServiceImpl companyAuthService;

    @PostMapping("/email") // 회사 이메일 인증 요청 API
    public ResponseEntity<CompanyEmailResponse> inputCompanyEMail(
            @Valid @RequestBody CompanyEmailRequest reqDto // 회사 이메일 검증을 위한 요청 데이터
    ) {
        companyAuthService.sendAuthCode(reqDto.companyEmail()); // 회사 이메일로 인증 코드 발송

        return ResponseEntity.ok(
                new CompanyEmailResponse(
                        reqDto.companyEmail(), // 요청된 회사 이메일
                        "company complete received it by email"
                )
        );
    }

    @PostMapping("/verify") // 회사 이메일 인증 코드 검증 API
    public ResponseEntity<CompanyVerifyResponse> verifyEmail(
            @Valid @RequestBody CompanyVerifyRequest reqDto, // 이메일 및 인증 코드 검증 요청 데이터
            @RequestHeader("X-USER-SQ") Long userSq
    ) {
        boolean result = companyAuthService.verify(
                reqDto.companyEmail(), // 인증 대상 회사 이메일
                reqDto.authCode()      // 사용자가 입력한 인증 코드
        );

        if (!result) {
            return ResponseEntity.ok(
                    new CompanyVerifyResponse(
                            false, // 인증 실패
                            "Invalid or expired verification code."
                    )
            );
        }
        //여기 지나면 성공이니까, 바로 COMPANY로 권한 변경
        accountService.updateUserRoleBySq(userSq, UserRole.COMPANY);
        return ResponseEntity.ok(
                new CompanyVerifyResponse(
                        true, // 인증 성공
                        "이메일 인증 성공." // 인증 성공 메시지
                )
        );
    }
}
