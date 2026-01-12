package io.why503.accountservice.domain.auth.ctrl;

import io.why503.accountservice.domain.account.model.dto.UpsertAccountReq;
import io.why503.accountservice.domain.account.model.ett.Account;
import io.why503.accountservice.domain.account.sv.AccountSv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
접근하고 로그인관련 url controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class LoginCtrl {
    private final AccountSv accountSv;

    //회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<Account> signUp(
            @RequestBody UpsertAccountReq request
    ){
        Account savedAccount = accountSv.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedAccount);
    }
}