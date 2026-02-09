package io.why503.accountservice.domain.accounts.controller;


import io.why503.accountbase.model.enums.UserRole;
import io.why503.accountservice.domain.accounts.model.dto.requests.GrantAccountRequest;
import io.why503.accountservice.domain.accounts.model.dto.requests.PointUseRequest;
import io.why503.accountservice.domain.accounts.model.dto.requests.CreateAccountRequest;
import io.why503.accountservice.domain.accounts.model.dto.response.UserCompanyResponse;
import io.why503.accountservice.domain.accounts.model.dto.response.UserPointResponse;
import io.why503.accountservice.domain.accounts.model.dto.response.UserRoleResponse;
import io.why503.accountservice.domain.accounts.service.AccountService;
import io.why503.accountservice.domain.accounts.util.AccountExceptionFactory;
import io.why503.accountservice.domain.companies.model.entity.Company;
import io.why503.accountservice.domain.companies.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountsController {

    private final AccountService accountService;

    private final CompanyService companyService;
    //생성
    @PostMapping
    public ResponseEntity<UserRoleResponse> create(
            @RequestBody @Valid CreateAccountRequest request
    ){
        UserRoleResponse savedAccount = accountService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedAccount);
    }
    //모두 조회
    @GetMapping
    public ResponseEntity<List<UserRoleResponse>> readAll(){
        List<UserRoleResponse> foundAccountList = accountService.readAll();
        return ResponseEntity.ok(foundAccountList);
    }
    //sq기준 조회
    @GetMapping("/sq/{sq}")
    public ResponseEntity<UserRoleResponse> readBySq(
            @PathVariable Long sq
    ){
        UserRoleResponse foundAccount = accountService.readBySq(sq);
        return ResponseEntity.ok(foundAccount);
    }
    //id기준 조회
    @GetMapping("/id/{id}")
    public ResponseEntity<UserRoleResponse> readById(
            @PathVariable String id
    ){
        UserRoleResponse foundAccount = accountService.readById(id);
        return ResponseEntity.ok(foundAccount);
    }

    //sq기준 포인트 조회
    @GetMapping("/point/{sq}")
    public ResponseEntity<UserPointResponse> readPointBySq(
            @PathVariable Long sq
    ){
        UserPointResponse foundPoint = accountService.readPointBySq(sq);
        return ResponseEntity.ok(foundPoint);
    }

    //sq기준 회사 시퀸스 넘버 조회
    @GetMapping("/company/{sq}")
    public ResponseEntity<UserCompanyResponse> readCompanySqBySq(
            @PathVariable Long sq
    ){
        UserCompanyResponse foundCompanySq = accountService.readCompanyBySq(sq);
        //null이면 not found
        if(foundCompanySq == null){
            throw AccountExceptionFactory.accountNotFound("don't have Company");
        }
        return ResponseEntity.ok(foundCompanySq);
    }
    //회사 가입(이걸로 가입하면 무조건 STAFF)
    @PatchMapping("/company/join/{companySq}")
    public ResponseEntity<UserRoleResponse> joinCompany(
            @PathVariable Long companySq,
            @RequestHeader("X-USER-SQ") Long userSq
    ){
        Company company = companyService.readCompanyBySq(companySq);
        UserRoleResponse response = accountService.joinCompany(userSq, company, UserRole.STAFF);
        return ResponseEntity.ok(response);
    }
    //회사 탈퇴
    @PatchMapping("/company/leave")
    public ResponseEntity<UserRoleResponse> leaveCompany(
            @RequestHeader("X-USER-SQ") Long userSq
    ){
        UserRoleResponse response = accountService.leaveCompany(userSq);
        return ResponseEntity.ok(response);
    }
    //권한 변경
    @PatchMapping("/grant")
    public ResponseEntity<UserRoleResponse> grantAccount(
            @RequestBody @Valid GrantAccountRequest request

    ){
        accountService.grantAccount(request.sq(), request.role());
        return ResponseEntity.ok().build();
    }
    //point 증가
    @PostMapping("/point/increase/{sq}")
    public ResponseEntity<UserRoleResponse> increasePoint(
            @PathVariable Long sq,
            @RequestBody @Valid PointUseRequest request
    ){
        UserRoleResponse updatedAccount = accountService.increasePoint(sq, request.amount());
        return ResponseEntity.ok(updatedAccount);
    }
    //point 감소
    @PostMapping("/point/decrease/{sq}")
    public ResponseEntity<UserRoleResponse> decreasePoint(
            @PathVariable Long sq,
            @RequestBody @Valid PointUseRequest request
    ){
        UserRoleResponse updatedAccount = accountService.decreasePoint(sq, request.amount());
        return ResponseEntity.ok(updatedAccount);
    }

    //sq기준 삭제
    @DeleteMapping("/sq/{sq}")
    public ResponseEntity<UserRoleResponse> delete(
            @PathVariable Long sq
    ){
        UserRoleResponse deletedAccount = accountService.deleteBySq(sq);
        return ResponseEntity.ok(deletedAccount);
    }

    //id기준 삭제
    @DeleteMapping("/id/{id}")
    public ResponseEntity<UserRoleResponse> delete(
            @PathVariable String id
    ){
        UserRoleResponse deletedAccount = accountService.deleteById(id);
        return ResponseEntity.ok(deletedAccount);
    }
}
