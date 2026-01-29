/**
 * 회사 등록 요청 DTO
 * 사용 목적 :
 * - 회사 등록 시 필요한 기본 정보 전달
 * - 결제/정산을 위한 계좌 정보 포함
 */
package io.why503.accountservice.domain.companies.model.dto.requset;

import io.why503.accountservice.domain.companies.model.enums.CompanyBank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CompanyRequest(
        @NotNull CompanyBank companyBank,     // 회사 정산 계좌 은행
        @NotBlank String accountNumber,       // 회사 정산 계좌 번호
        @NotBlank String companyName,         // 회사명
        @NotBlank String ownerName,           // 대표자명
        @NotBlank String companyPhone,        // 회사 연락처
        @NotBlank String companyEmail,        // 회사 대표 이메일
        @NotBlank String companyBasicAddr,    // 회사 기본 주소
        @NotBlank String companyDetailAddr,   // 회사 상세 주소
        @NotBlank String companyPost,         // 회사 우편번호
        @NotNull LocalDateTime amountDate
) { }
