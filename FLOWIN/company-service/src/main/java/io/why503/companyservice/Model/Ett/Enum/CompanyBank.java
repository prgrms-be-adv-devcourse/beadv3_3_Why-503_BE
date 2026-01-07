package io.why503.companyservice.Model.Ett.Enum;

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

    private final String displayName;

    CompanyBank(String displayName) {
        this.displayName = displayName;
    }
}
