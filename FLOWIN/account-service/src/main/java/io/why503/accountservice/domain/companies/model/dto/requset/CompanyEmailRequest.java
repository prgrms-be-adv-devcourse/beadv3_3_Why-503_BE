/**
 * 회사 이메일 입력 요청 DTO
 * 사용 목적 :
 * - 회사 이메일 인증 요청 시 입력값 전달
 * - 이메일 형식 및 길이 사전 검증
 */

package io.why503.accountservice.domain.companies.model.dto.requset;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyEmailRequest (
    @NotBlank(message = "회사 이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 100, message = "이메일은 100자 이내여야 합니다.")
    String companyEmail) { }
