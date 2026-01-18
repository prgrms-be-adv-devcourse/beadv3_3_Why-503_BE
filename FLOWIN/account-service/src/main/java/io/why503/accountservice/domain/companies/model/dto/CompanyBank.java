/**
 * 회사 정산 은행 코드 Enum
 *
 * 사용 목적 :
 * - 회사 등록 시 은행 값을 고정된 코드로 관리
 * - 잘못된 은행 값 입력 방지
 */
package io.why503.accountservice.domain.companies.model.dto;

import lombok.Getter;

@Getter
public enum CompanyBank {

    KB("국민은행"),
    SHINHAN("신한은행"),
    WOORI("우리은행"),
    HANA("하나은행"),
    NH("농협"),
    IBK("기업은행"),
    KAKAO("카카오뱅크"),
    TOSS("토스뱅크");

    private final String displayName; // 사용자 화면에 표시할 은행명

    CompanyBank(String displayName) {
        this.displayName = displayName; // 은행 코드와 표시명 매핑
    }
}
