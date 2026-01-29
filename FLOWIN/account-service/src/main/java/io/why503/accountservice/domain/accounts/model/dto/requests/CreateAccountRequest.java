package io.why503.accountservice.domain.accounts.model.dto.requests;

import io.why503.accountservice.domain.accounts.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/*
http에서 받아오는 역할
 */
public record CreateAccountRequest(
        @NotBlank String userId,
        @NotBlank String userPassword,
        @NotBlank String userName,
        @NotNull LocalDateTime birthday,
        @NotNull Gender gender,
        @NotBlank String userPhone,
        @NotBlank String userEmail,
        @NotBlank String userBasicAddr,
        @NotBlank String userDetailAddr,
        @NotBlank String userPost
) { }