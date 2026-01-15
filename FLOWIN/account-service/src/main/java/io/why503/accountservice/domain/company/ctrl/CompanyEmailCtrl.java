/**
 * Company Email Controller
 * 회사 이메일 인증을 시작하는 API 컨트롤러
 * 사용 목적 :
 * - 회사 이메일 입력 요청 처리
 * - 이메일 인증 코드 발송 트리거
 */
package io.why503.accountservice.domain.company.ctrl;

import io.why503.accountservice.domain.company.model.dto.req.CompanyEmailReqDto;
import io.why503.accountservice.domain.company.model.dto.res.CompanyEmailResDto;
import io.why503.accountservice.domain.company.sv.impl.CompanyEmailAuthSvImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Slf4j
@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyEmailCtrl {

    private final CompanyEmailAuthSvImpl companyEmailAuthService; // 이메일 인증 비즈니스 로직 처리 Service

    @PostMapping("/email") // 회사 이메일 인증 요청 API
    public ResponseEntity<CompanyEmailResDto> inputCompanyEMail(
            @Valid @RequestBody CompanyEmailReqDto reqDto // 회사 이메일 검증을 위한 요청 데이터
    ) {
        companyEmailAuthService.sendAuthCode(reqDto.getCompanyEmail()); // 회사 이메일로 인증 코드 발송

        return ResponseEntity.ok(
                new CompanyEmailResDto(
                        reqDto.getCompanyEmail(), // 요청된 회사 이메일
                        "company complete received it by email"
                )
        );
    }
}
