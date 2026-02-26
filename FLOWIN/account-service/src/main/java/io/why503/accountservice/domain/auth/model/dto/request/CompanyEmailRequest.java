/**
 * 회사 이메일 입력 요청 DTO
 * 사용 목적 :
 * - 회사 이메일 인증 요청 시 입력값 전달
 * - 이메일 형식 및 길이 사전 검증
 */

package io.why503.accountservice.domain.auth.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyEmailRequest (
    @NotBlank(message = "이메일")
    @Email(message = "이메일(형식)")
    @Size(max = 100, message = "이메일(길이)")
    String companyEmail) { }
