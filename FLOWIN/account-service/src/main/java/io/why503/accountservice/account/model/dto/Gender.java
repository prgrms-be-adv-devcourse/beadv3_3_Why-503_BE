package io.why503.accountservice.account.model.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Getter
public enum Gender {
    MALE(0), FEMALE(1);

    private final Integer code;

    private static final Map<Integer, Gender> map = initMap();

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

    public static Gender getGender(Integer code) throws NoSuchElementException {
        Gender gender = map.get(code);
        if(gender == null){
            throw new NoSuchElementException("Gender.getGender(" + code + " ) is not found");
        }
        return gender;
    }
    @JsonCreator
    public static Gender from(Integer code) {
        return Gender.getGender(code);
    }

    @JsonValue
    public Integer toJson() {
        return code;
    }
}


