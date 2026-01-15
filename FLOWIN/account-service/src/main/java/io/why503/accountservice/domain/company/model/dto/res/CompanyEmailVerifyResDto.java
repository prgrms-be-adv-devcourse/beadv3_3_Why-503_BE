/**
 * 회사 이메일 인증 코드 검증 응답 DTO
 * 사용 목적 :
 * - 이메일 인증 성공/실패 여부 전달
 * - 인증 결과 메시지 반환
 */
package io.why503.accountservice.domain.company.model.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompanyEmailVerifyResDto {

    private boolean verified; // 이메일 인증 성공 여부
    private String message;   // 인증 결과 안내 메시지
}
