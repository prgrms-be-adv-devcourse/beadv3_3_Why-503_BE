package io.why503.accountservice.Model.Dto;

import io.why503.accountservice.Model.Ett.Enum.Gender;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record UpsertAccountDto(
        @NotBlank String id,            @NotBlank String password,
        @NotBlank String name,          @NotBlank LocalDateTime birthday,
        @NotBlank Gender gender,        @NotBlank String phone,
        @NotBlank String email,         @NotBlank String basicAddr,
        @NotBlank String detailAddr,    @NotBlank String post
) { }
