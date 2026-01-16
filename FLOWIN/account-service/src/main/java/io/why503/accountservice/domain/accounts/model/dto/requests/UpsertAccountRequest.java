package io.why503.accountservice.domain.accounts.model.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/*
http에서 받아오는 역할
 */
public record UpsertAccountRequest(
        @NotBlank String id,
        @NotBlank String password,
        @NotBlank String name,
        @NotNull LocalDateTime birthday,
        @NotNull int gender,
        @NotBlank String phone,
        @NotBlank String email,
        @NotBlank String basicAddr,
        @NotBlank String detailAddr,
        @NotBlank String post
) {
}
