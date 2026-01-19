package io.why503.accountservice.domain.accounts.controller;


import io.why503.accountservice.domain.accounts.model.dto.requests.UpsertAccountRequest;
import io.why503.accountservice.domain.accounts.model.dto.response.UserCompanyResponse;
import io.why503.accountservice.domain.accounts.model.dto.response.UserPointResponse;
import io.why503.accountservice.domain.accounts.model.dto.response.UserSummaryResponse;
import io.why503.accountservice.domain.accounts.model.entity.Account;
import io.why503.accountservice.domain.accounts.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


// HATEOAS
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountsController {

    private final AccountService accountService;
    //생성
    @PostMapping
    public ResponseEntity<UserSummaryResponse> create(
            @RequestBody UpsertAccountRequest request
    ){
        UserSummaryResponse savedAccount = accountService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedAccount);
    }
    //모두 조회
    @GetMapping
    public ResponseEntity<List<UserSummaryResponse>> readAll(){
        List<UserSummaryResponse> foundAccountList = accountService.readAll();
        return ResponseEntity.ok(foundAccountList);
    }
    //sq기준 조회
    @GetMapping("/sq/{sq}")
    public ResponseEntity<UserSummaryResponse> readBySq(
            @PathVariable Long sq
    ){
        UserSummaryResponse foundAccount = accountService.readBySq(sq);
        return ResponseEntity.ok(foundAccount);
    }
    //id기준 조회
    @GetMapping("/id/{id}")
    public ResponseEntity<UserSummaryResponse> readById(
            @PathVariable String id
    ){
        UserSummaryResponse foundAccount = accountService.readById(id);
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
        if(foundCompanySq == null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(foundCompanySq);
    }


    //sq기준 수정
    @PatchMapping("/sq/{sq}")
    public ResponseEntity<UserSummaryResponse> updateBySq(
            @PathVariable Long sq,
            @RequestBody UpsertAccountRequest request
    ){
        UserSummaryResponse updatedAccount = accountService.updateBySq(sq, request);
        return ResponseEntity.ok(updatedAccount);
    }
    //id기준 수정
    @PatchMapping("/id/{id}")
    public ResponseEntity<UserSummaryResponse> updateById(
            @PathVariable String id,
            @RequestBody UpsertAccountRequest request
    ){
        UserSummaryResponse updatedAccount = accountService.updateById(id, request);
        return ResponseEntity.ok(updatedAccount);
    }


    //sq기준 삭제
    @DeleteMapping("/sq/{sq}")
    public ResponseEntity<UserSummaryResponse> delete(
            @PathVariable Long sq
    ){
        UserSummaryResponse deletedAccount = accountService.deleteBySq(sq);
        return ResponseEntity.ok(deletedAccount);
    }


    //id기준 삭제
    @DeleteMapping("/id/{id}")
    public ResponseEntity<UserSummaryResponse> delete(
            @PathVariable String id
    ){
        UserSummaryResponse deletedAccount = accountService.deleteById(id);
        return ResponseEntity.ok(deletedAccount);
    }
}
