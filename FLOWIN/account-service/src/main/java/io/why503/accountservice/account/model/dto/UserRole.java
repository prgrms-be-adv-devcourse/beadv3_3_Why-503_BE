package io.why503.accountservice.account.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Getter
public enum UserRole {
    ADMIN(0), USER(1), COMPANY(2);

    private final Integer code;

    private static final Map<Integer, UserRole> map = initMap();

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

    public static UserRole getUserRole(Integer code) throws NoSuchElementException {
        UserRole userRole = map.get(code);
        if(userRole == null){
            throw new NoSuchElementException("UserRole.getUserRole(" + code + " ) is not found");
        }
        return userRole;
    }
    @JsonCreator
    public static UserRole from(Integer code) {
        return UserRole.getUserRole(code);
    }

    @JsonValue
    public Integer toJson() {
        return code;
    }
}
