/**
 * 회사 이메일 인증 코드 검증 요청 DTO
 * 사용 목적 :
 * - 회사 이메일과 인증 코드 전달
 * - 이메일 인증 여부 검증 요청
 */
package io.why503.accountservice.domain.auth.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CompanyVerifyRequest(
        @NotBlank(message = "이메일") // 이메일 미입력 방지
        @Email(message = "이메일 형식")    // 이메일 형식 검증
        String companyEmail, // 인증 대상 회사 이메일
        @NotBlank(message = "인증 코드")  // 인증 코드 미입력 방지
        String authCode    // 사용자가 입력한 이메일 인증 코드
) { }
