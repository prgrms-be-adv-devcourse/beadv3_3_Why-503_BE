package io.why503.companyservice.Ctrl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.why503.companyservice.Model.Dto.CompanyEmailVerifyReqDto;
import io.why503.companyservice.Model.Dto.CompanyEmailVerifyResDto;
import io.why503.companyservice.Sv.CompanyEmailVerifyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/company/email")
@RequiredArgsConstructor
public class CompanyEmailVerifyController {
    private final CompanyEmailVerifyService verifyService;

    @PostMapping("/verify")
    public ResponseEntity<CompanyEmailVerifyResDto> verifyEmail(
        @Valid @RequestBody CompanyEmailVerifyReqDto reqDto
    ) {
        boolean result = verifyService.verify(
            reqDto.getCompanyEmail(),
            reqDto.getAuthCode()
        );
        if (!result) {
            return ResponseEntity.ok(
                new CompanyEmailVerifyResDto(
                    false,
                    "Invalid or expired verification code."
                )
            );
        }
        return ResponseEntity.ok(
            new CompanyEmailVerifyResDto(
                true,
                "이메일 인증 성공."
            )
        );
    }
}