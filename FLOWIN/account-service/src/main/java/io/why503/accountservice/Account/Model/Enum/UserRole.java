package io.why503.accountservice.Account.Model.Enum;

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
}
