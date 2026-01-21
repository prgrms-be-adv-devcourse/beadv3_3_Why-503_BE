package io.why503.accountbase.util.converter;

import io.why503.accountbase.model.enums.UserStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
/*
enum 변환기 vec.UserStat
java <-> sql에서 작동함
* */
@Converter(autoApply = false)
public class UserStatusConverter implements AttributeConverter<UserStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(UserStatus userStatus) {
        if(userStatus == null){
            return null;
        }
        return userStatus.getCode();
    }

    @Override
    public UserStatus convertToEntityAttribute(Integer code) {
        if(code == null){
            return null;
        }
        return UserStatus.getUserStat(code);
    }
}
