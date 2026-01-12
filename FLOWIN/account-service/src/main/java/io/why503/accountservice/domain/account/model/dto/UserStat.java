package io.why503.accountservice.domain.account.model.dto;

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
public enum UserStat {
    NORMAL(0), WITHDRAW(1);

    private final Integer code;

    private static final Map<Integer, UserStat> map = initMap();
    /*
        가능한 UserStat 을 찾기 쉽게 hashmap으로 선언
    */
    private static Map<Integer, UserStat> initMap(){
        Map<Integer, UserStat> userStatMap = new HashMap<>();
        for(UserStat i : UserStat.values()){
            userStatMap.put(i.code, i);
        }
        return userStatMap;
    }

    UserStat(Integer code){
        this.code = code;
    }
    /*
        현재 UserStat 을 enum( UserStat )으로 반환
    */
    public static UserStat getUserStat(Integer code) throws NoSuchElementException {
        UserStat userStat = map.get(code);
        if(userStat == null){
            throw new NoSuchElementException("UserStat.getUserStat(" + code + " ) is not found");
        }
        return userStat;
    }
    /*
        enum 변환기
        json -> java에서 작동함
    * */
    @JsonCreator
    public static UserStat from(Integer code) {
        return UserStat.getUserStat(code);
    }

    @JsonValue
    public Integer toJson() {
        return code;
    }
}
