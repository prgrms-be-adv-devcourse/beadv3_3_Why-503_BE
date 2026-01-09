package io.why503.accountservice.account.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Getter
public enum UserStat {
    NORMAL(0), WITHDRAW(1);

    private final Integer code;

    private static final Map<Integer, UserStat> map = initMap();

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

    public static UserStat getUserStat(Integer code) throws NoSuchElementException {
        UserStat userStat = map.get(code);
        if(userStat == null){
            throw new NoSuchElementException("UserStat.getUserStat(" + code + " ) is not found");
        }
        return userStat;
    }
    @JsonCreator
    public static UserStat from(Integer code) {
        return UserStat.getUserStat(code);
    }

    @JsonValue
    public Integer toJson() {
        return code;
    }
}
