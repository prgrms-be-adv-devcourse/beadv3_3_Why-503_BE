package io.why503.accountbase.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
/*
enum은 어차피 안에 있는 속성의 개수에 따라 초기에 jvm이 알아서 생성
 */
public enum Gender {
    MALE("MALE"), FEMALE("FEMALE");

    private final String code;


    Gender(String code){
        this.code = code;
    }
    /*
        enum 변환기
        json <-> java에서 작동함
    */
    @JsonCreator
    public static Gender fromJson(String code) {
        return Gender.valueOf(code.toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return code;
    }

}