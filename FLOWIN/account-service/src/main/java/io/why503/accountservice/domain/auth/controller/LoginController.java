package io.why503.accountservice.domain.auth.controller;

import io.why503.accountservice.domain.accounts.model.dto.requests.UpsertAccountRequest;
import io.why503.accountservice.domain.accounts.model.dto.response.UserRoleResponse;
import io.why503.accountservice.domain.accounts.service.AccountService;
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
@RequestMapping("/auth")
public class LoginController {
    private final AccountService accountService;

    //회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<UserRoleResponse> signUp(
            @RequestBody UpsertAccountRequest request
    ){
        UserRoleResponse savedAccount = accountService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedAccount);
    }
}