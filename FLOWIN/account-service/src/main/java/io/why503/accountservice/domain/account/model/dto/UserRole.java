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
public enum UserRole {
    ADMIN(0), USER(1), COMPANY(2);

    private final Integer code;

    private static final Map<Integer, UserRole> map = initMap();
    /*
        가능한 UserRole을 찾기 쉽게 hashmap으로 선언
         */
    private static Map<Integer, UserRole> initMap(){
        Map<Integer, UserRole> userRoleMap = new HashMap<>();
        for(UserRole i : UserRole.values()){
            userRoleMap.put(i.code, i);
        }
        return userRoleMap;
    }

    UserRole(Integer code){
        this.code = code;
    }
    /*
        현재 UserRole 을 enum( UserRole )으로 반환
         */
    public static UserRole getUserRole(Integer code) throws NoSuchElementException {
        UserRole userRole = map.get(code);
        if(userRole == null){
            throw new NoSuchElementException("UserRole.getUserRole(" + code + " ) is not found");
        }
        return userRole;
    }
    /*
        enum 변환기
        json <-> java에서 작동함
    * */
    @JsonCreator
    public static UserRole from(Integer code) {
        return UserRole.getUserRole(code);
    }

    @JsonValue
    public Integer toJson() {
        return code;
    }
}
