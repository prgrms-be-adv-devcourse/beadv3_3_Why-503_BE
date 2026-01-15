/**
 * Company Email Verify Controller
 * 회사 이메일 인증 코드 검증을 처리하는 API 컨트롤러
 * 사용 목적 :
 * - 사용자가 입력한 인증 코드 검증
 * - 인증 성공/실패 여부 반환
 */
package io.why503.accountservice.domain.company.ctrl;

import io.why503.accountservice.domain.account.model.dto.UserRole;
import io.why503.accountservice.domain.account.sv.AccountSv;
import io.why503.accountservice.domain.company.model.dto.req.CompanyEmailVerifyReqDto;
import io.why503.accountservice.domain.company.model.dto.res.CompanyEmailVerifyResDto;
import io.why503.accountservice.domain.company.sv.impl.CompanyEmailVerifySvImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/company/email")
@RequiredArgsConstructor
public class CompanyEmailVerifyCtrl {

    private final CompanyEmailVerifySvImpl verifyService; // 이메일 인증 코드 검증 비즈니스 로직 처리 Service
    private final AccountSv accountSv;


    @PostMapping("/verify") // 회사 이메일 인증 코드 검증 API
    public ResponseEntity<CompanyEmailVerifyResDto> verifyEmail(
            @Valid @RequestBody CompanyEmailVerifyReqDto reqDto, // 이메일 및 인증 코드 검증 요청 데이터
            @RequestHeader("X-USER-SQ") Long sq
    ) {
        boolean result = verifyService.verify(
                reqDto.getCompanyEmail(), // 인증 대상 회사 이메일
                reqDto.getAuthCode()      // 사용자가 입력한 인증 코드
        );

        if (!result) {
            return ResponseEntity.ok(
                    new CompanyEmailVerifyResDto(
                            false, // 인증 실패
                            "Invalid or expired verification code."
                    )
            );
        }
        //여기 지나면 성공이니까, 바로 COMPANY로 권한 변경
        accountSv.updateUserRoleBySq(sq, UserRole.COMPANY);
        return ResponseEntity.ok(
                new CompanyEmailVerifyResDto(
                        true, // 인증 성공
                        "이메일 인증 성공." // 인증 성공 메시지
                )
        );
    }
}
