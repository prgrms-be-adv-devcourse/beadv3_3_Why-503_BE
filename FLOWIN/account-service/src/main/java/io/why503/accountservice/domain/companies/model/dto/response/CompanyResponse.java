/**
 * 회사 정보 조회 응답 DTO
 * 사용 목적 :
 * - 회사 단건 조회 결과 반환
 * - Company Entity 데이터를 외부 응답용으로 변환
 */
package io.why503.accountservice.domain.companies.model.dto.response;

import io.why503.accountservice.domain.companies.model.enums.CompanyBank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CompanyResponse(
        @NotNull Long companySq,            // 회사 식별자
        @NotNull CompanyBank companyBank,   // 회사 정산 은행
        @NotBlank String accountNumber,     // 회사 정산 계좌 번호
        @NotBlank String companyName,       // 회사명
        @NotBlank String ownerName,         // 대표자명
        @NotBlank String companyPhone,      // 회사 연락처
        @NotBlank String companyEmail,      // 회사 대표 이메일
        @NotBlank String companyBasicAddr,  // 회사 기본 주소
        @NotBlank String companyDetailAddr, // 회사 상세 주소
        @NotBlank String companyPost,       // 회사 우편번호
        @NotNull Long amount,               // 회사 정산 금액
        @NotNull LocalDateTime amountDate   // 정산 금액 등록/변경 시각
) { }
