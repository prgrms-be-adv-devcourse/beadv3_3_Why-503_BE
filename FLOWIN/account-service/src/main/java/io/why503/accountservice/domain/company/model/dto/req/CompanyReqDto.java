/**
 * 회사 등록 요청 DTO
 * 사용 목적 :
 * - 회사 등록 시 필요한 기본 정보 전달
 * - 결제/정산을 위한 계좌 정보 포함
 */
package io.why503.accountservice.domain.company.model.dto.req;

import io.why503.accountservice.domain.company.model.dto.CompanyBank;
import lombok.Getter;

@Getter
public class CompanyReqDto {

    private CompanyBank companyBank; // 회사 정산 계좌 은행
    private String accountNumber;          // 회사 정산 계좌 번호

    private String companyName;      // 회사명
    private String ownerName;        // 대표자명
    private String companyPhone;     // 회사 연락처
    private String companyEmail;     // 회사 대표 이메일
    private String companyAddr;      // 회사 주소
    private String companyPost;      // 회사 우편번호

    private Long amount;             // 초기 정산 금액 또는 등록 금액
}
