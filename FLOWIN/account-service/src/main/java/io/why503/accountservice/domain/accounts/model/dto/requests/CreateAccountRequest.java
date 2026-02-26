package io.why503.accountservice.domain.accounts.model.dto.requests;

import io.why503.accountbase.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/*
http에서 받아오는 역할
 */
public record CreateAccountRequest(
        @NotBlank(message = "아이디")
        String userId,
        @NotBlank(message = "비밀번호")
        String userPassword,
        @NotBlank(message = "이름")
        String userName,
        @NotNull(message = "생일")
        LocalDateTime birthday,
        @NotNull(message = "성별")
        Gender gender,
        @NotBlank(message = "전화번호")
        String userPhone,
        @NotBlank(message = "이메일")
        String userEmail,
        @NotBlank(message = "기본주소")
        String userBasicAddr,
        @NotBlank(message = "상세주소")
        String userDetailAddr,
        @NotBlank(message = "우편번호")
        String userPost
) { }