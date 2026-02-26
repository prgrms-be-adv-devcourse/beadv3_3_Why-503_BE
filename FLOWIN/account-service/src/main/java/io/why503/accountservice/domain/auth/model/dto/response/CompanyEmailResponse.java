/**
 * 회사 이메일 인증 요청 응답 DTO
 * 사용 목적 :
 * - 회사 이메일 인증 요청 결과 응답
 * - 요청된 이메일과 처리 결과 메시지 전달
 */
package io.why503.accountservice.domain.auth.model.dto.response;

import jakarta.validation.constraints.NotBlank;

public record CompanyEmailResponse(
        @NotBlank String companyEmail,    // 요청된 회사 이메일
        @NotBlank String message          // 이메일 접수 결과 메시지
) { }
