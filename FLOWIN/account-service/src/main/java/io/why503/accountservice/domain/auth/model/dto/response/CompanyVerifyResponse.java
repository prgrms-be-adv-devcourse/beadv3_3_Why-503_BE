/**
 * 회사 이메일 인증 코드 검증 응답 DTO
 * 사용 목적 :
 * - 이메일 인증 성공/실패 여부 전달
 * - 인증 결과 메시지 반환
 */
package io.why503.accountservice.domain.auth.model.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompanyVerifyResponse(
        @NotNull boolean verified,   // 이메일 인증 성공 여부
        @NotBlank String message      // 인증 결과 안내 메시지
) { }
