package io.why503.accountservice.domain.accounts.model.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/*
http에서 받아오는 역할
 */
public record UpsertAccountRequest(
        @NotBlank String userId,
        @NotBlank String userPassword,
        @NotBlank String userName,
        @NotNull LocalDateTime birthday,
        @NotNull int gender,
        @NotBlank String userPhone,
        @NotBlank String userEmail,
        @NotBlank String userBasicAddr,
        @NotBlank String userDetailAddr,
        @NotBlank String userPost
) {
}
