package io.why503.companyservice.Ctrl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.why503.companyservice.Model.Dto.CompanyEmailReqDto;
import io.why503.companyservice.Model.Dto.CompanyEmailResDto;
import io.why503.companyservice.Sv.CompanyEmailAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyEmailController {

    private final CompanyEmailAuthService companyEmailAuthService;

    @PostMapping("/email")
    public ResponseEntity<CompanyEmailResDto> inputCompanyEMail(
        @Valid @RequestBody CompanyEmailReqDto reqDto
    ) {
        companyEmailAuthService.sendAuthCode(reqDto.getCompanyEmail());
        return ResponseEntity.ok(
            new CompanyEmailResDto(
                reqDto.getCompanyEmail(), "회사 이메일이 정상적으로 접수되었습니다")
        );
    }
}
