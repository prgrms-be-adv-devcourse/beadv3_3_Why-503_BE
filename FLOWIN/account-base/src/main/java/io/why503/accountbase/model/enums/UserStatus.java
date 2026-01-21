package io.why503.accountbase.model.enums;

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
public enum UserStatus {
    NORMAL(0), WITHDRAW(1);

    private final Integer code;

    private static final Map<Integer, UserStatus> map = initMap();
    /*
        가능한 UserStat 을 찾기 쉽게 hashmap으로 선언
    */
    private static Map<Integer, UserStatus> initMap(){
        Map<Integer, UserStatus> userStatMap = new HashMap<>();
        for(UserStatus i : UserStatus.values()){
            userStatMap.put(i.code, i);
        }
        return userStatMap;
    }

    UserStatus(Integer code){
        this.code = code;
    }
    /*
        현재 UserStat 을 enum( UserStat )으로 반환
    */
    public static UserStatus getUserStat(Integer code) throws NoSuchElementException {
        UserStatus userStatus = map.get(code);
        if(userStatus == null){
            throw new NoSuchElementException("UserStat.getUserStat(" + code + " ) is not found");
        }
        return userStatus;
    }
    /*
        enum 변환기
        json <-> java에서 작동함
    * */
    @JsonCreator
    public static UserStatus from(Integer code) {
        return UserStatus.getUserStat(code);
    }

    @JsonValue
    public Integer toJson() {
        return code;
    }
}
