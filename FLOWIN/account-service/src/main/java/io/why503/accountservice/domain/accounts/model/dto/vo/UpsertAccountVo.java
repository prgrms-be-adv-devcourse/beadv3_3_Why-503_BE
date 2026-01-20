package io.why503.accountservice.domain.accounts.model.dto.vo;

import io.why503.accountservice.domain.accounts.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/*
Req -> Ett로 갈 때, password암호화를 위해서 사용,
암호화는 bean특성상 다른 곳에 선언
사용은 mapper에서 사용
 */
public record UpsertAccountVo(
    @NotBlank
    String userId,
    @NotBlank
    String userPassword,
    @NotBlank
    String userName,
    @NotNull
    LocalDateTime birthday,
    @NotNull
    Gender gender,
    @NotBlank
    String userPhone,
    @NotBlank
    String userEmail,
    @NotBlank
    String userBasicAddr,
    @NotBlank
    String userDetailAddr,
    @NotBlank
    String userPost
) { }
