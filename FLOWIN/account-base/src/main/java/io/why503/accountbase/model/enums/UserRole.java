package io.why503.accountbase.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/*
이넘은 어차피 안에 있는 속성의 개수에 따라 초기에 jvm이 알아서 생성
 */
public enum UserRole {
    ADMIN("ADMIN"), USER("USER"), STAFF("STAFF") ,COMPANY("COMPANY");

    private final String code;

    UserRole(String code){
        this.code = code;
    }
    /*
        enum 변환기
        json <-> java에서 작동함
    */
    @JsonCreator
    public static UserRole fromJson(String code) {
        return UserRole.valueOf(code.toUpperCase());
    }
    @JsonValue
    public String toJson() {
        return code;
    }
}