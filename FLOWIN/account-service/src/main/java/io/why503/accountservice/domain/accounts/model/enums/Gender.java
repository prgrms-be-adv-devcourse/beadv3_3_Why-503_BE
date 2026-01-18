package io.why503.accountservice.domain.accounts.model.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/*
enum은 어차피 안에 있는 속성의 개수에 따라 초기에 jvm이 알아서 생성
 */
@Getter
public enum Gender {
    MALE(0), FEMALE(1);

    private final Integer code;

    private static final Map<Integer, Gender> map = initMap();

    /*
    가능한 gender을 찾기 쉽게 hashmap으로 선언
     */
    private static Map<Integer, Gender> initMap(){
        Map<Integer, Gender> genderMap = new HashMap<>();
        for(Gender i : Gender.values()){
            genderMap.put(i.code, i);
        }
        return genderMap;
    }

    Gender(Integer code){
        this.code = code;
    }

    /*
    현재 gender을 enum(gender)으로 반환
     */
    public static Gender getGender(Integer code) throws NoSuchElementException {
        Gender gender = map.get(code);
        if(gender == null){
            throw new NoSuchElementException("Gender.getGender(" + code + " ) is not found");
        }
        return gender;
    }
    /*
        enum 변환기
        json <-> java에서 작동함
    * */
    @JsonCreator
    public static Gender from(Integer code) {
        return Gender.getGender(code);
    }

    @JsonValue
    public Integer toJson() {
        return code;
    }
}


