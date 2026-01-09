package io.why503.accountservice.domain.account.ctrl;


import io.why503.accountservice.domain.account.model.dto.UpsertAccountReq;
import io.why503.accountservice.domain.account.model.ett.Account;
import io.why503.accountservice.domain.account.sv.AccountSv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountCtrl {
    private final AccountSv accountSv;
    //생성
    @PostMapping
    public ResponseEntity<Account> create(
            @RequestBody UpsertAccountReq request
    ){
        Account savedAccount = accountSv.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedAccount);
    }
    //모두 조회
    @GetMapping
    public ResponseEntity<List<Account>> readAll(){
        List<Account> foundAccountList = accountSv.readAll();
        return ResponseEntity.ok(foundAccountList);
    }
    //sq기준 조회
    @GetMapping("/account-sq/{sq}")
    public ResponseEntity<Account> readBySq(
            @PathVariable Long sq
    ){
        Account foundAccount = accountSv.readBySq(sq);
        return ResponseEntity.ok(foundAccount);
    }
    //id기준 조회
    @GetMapping("/account-id/{id}")
    public ResponseEntity<Account> readById(
            @PathVariable String id
    ){
        Account foundAccount = accountSv.readById(id);
        return ResponseEntity.ok(foundAccount);
    }
    //sq기준 수정
    @PatchMapping("/account-sq/{sq}")
    public ResponseEntity<Account> updateBySq(
            @PathVariable Long sq,
            @RequestBody UpsertAccountReq request
    ){
        Account updatedAccount = accountSv.updateBySq(sq, request);
        return ResponseEntity.ok(updatedAccount);
    }
    //id기준 수정
    @PatchMapping("/account-id/{id}")
    public ResponseEntity<Account> updateById(
            @PathVariable String id,
            @RequestBody UpsertAccountReq request
    ){
        Account updatedAccount = accountSv.updateById(id, request);
        return ResponseEntity.ok(updatedAccount);
    }
    //sq기준 삭제
    @DeleteMapping("/account-sq/{sq}")
    public ResponseEntity<Account> delete(
            @PathVariable Long sq
    ){
        Account deletedAccount = accountSv.deleteBySq(sq);
        return ResponseEntity.ok(deletedAccount);
    }
    //id기준 삭제
    @DeleteMapping("/account-id/{id}")
    public ResponseEntity<Account> delete(
            @PathVariable String id
    ){
        Account deletedAccount = accountSv.deleteById(id);
        return ResponseEntity.ok(deletedAccount);
    }
}
