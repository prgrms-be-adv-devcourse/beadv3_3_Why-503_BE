package io.why503.accountservice.Account.Model.Dto;

import io.why503.accountservice.Account.Model.Enum.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpsertAccountReq(
        @NotBlank String id, @NotBlank String password,
        @NotBlank String name, @NotNull LocalDateTime birthday,
        @NotNull Gender gender, @NotBlank String phone,
        @NotBlank String email, @NotBlank String basicAddr,
        @NotBlank String detailAddr, @NotBlank String post
) {
}
