/**
 * 회사 이메일 인증 요청 응답 DTO
 * 사용 목적 :
 * - 회사 이메일 인증 요청 결과 응답
 * - 요청된 이메일과 처리 결과 메시지 전달
 */
package io.why503.accountservice.domain.companies.model.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompanyEmailResDto {

    private String companyEmail; // 요청된 회사 이메일
    private String message;      // 이메일 접수 결과 메시지
}
