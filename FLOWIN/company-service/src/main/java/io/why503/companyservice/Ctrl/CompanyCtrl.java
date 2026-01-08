// CompanyController.java

package io.why503.companyservice.Ctrl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.why503.companyservice.Model.Dto.CompanyReqDto;
import io.why503.companyservice.Model.Dto.CompanyResDto;
import io.why503.companyservice.Sv.CompanySv;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyCtrl {

    private final CompanySv companyService;

    // 회사 등록
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Void> registerCompany(
            @RequestBody CompanyReqDto requestDto) {

        companyService.registerCompany(requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{companySq}")
    public ResponseEntity<CompanyResDto> getCompany(
        @PathVariable("companySq") Long companySq) {

    return ResponseEntity.ok(
            companyService.getCompanyByCompanySq(companySq)
    );
    }


    // // 회원 시퀀스로 조회
    // @GetMapping("/user/{userSq}")
    // public ResponseEntity<CompanyResDto> getCompanyByUser(
    //         @PathVariable Long userSq) {

    //     return ResponseEntity.ok(
    //             companyService.getCompanyByUserSq(userSq)
    //     );
    // }
}
