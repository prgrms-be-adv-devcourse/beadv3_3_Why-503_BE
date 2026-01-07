package io.why503.accountservice.Ctrl;


import io.why503.accountservice.Model.Dto.UpsertAccountDto;
import io.why503.accountservice.Model.Ett.Account;
import io.why503.accountservice.Sv.AccountSv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountCtrl {
    private final AccountSv accountSv;

    @PostMapping
    public ResponseEntity<Account> create(
            @RequestBody UpsertAccountDto request
    ){
        Account savedAccount = accountSv.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedAccount);
    }

    @GetMapping
    public ResponseEntity<List<Account>> readAll(){
        List<Account> foundAccountList = accountSv.readAll();
        return ResponseEntity.ok(foundAccountList);
    }

    @GetMapping("/accountSq/{accountSq}")
    public ResponseEntity<Account> readBySq(
            @PathVariable Long accountSq
    ){
        Account foundAccount = accountSv.readBySq(accountSq);
        return ResponseEntity.ok(foundAccount);
    }
    @GetMapping("/accountId/{accountId}")
    public ResponseEntity<Account> readById(
            @PathVariable String accountId
    ){
        Account foundAccount = accountSv.readById(accountId);
        return ResponseEntity.ok(foundAccount);
    }
    @PatchMapping("/patch/{accountId}")
    public ResponseEntity<Account> create(
            @PathVariable String accountId,
            @RequestBody UpsertAccountDto request
    ){
        Account updatedAccount = accountSv.update(accountId , request);
        return ResponseEntity.ok(updatedAccount);
    }
    @DeleteMapping("/delete/{accountId}")
    public ResponseEntity<Account> delete(
            @PathVariable String accountId
    ){
        Account deletedAccount = accountSv.delete(accountId);
        return ResponseEntity.ok(deletedAccount);
    }
}
