package io.why503.accountservice.Model.Enum.EnumConverter;

import io.why503.accountservice.Model.Enum.UserStat;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class UserStatConverter implements AttributeConverter<UserStat, Integer> {
    @Override
    public Integer convertToDatabaseColumn(UserStat userStat) {
        if(userStat == null){
            return null;
        }
        return userStat.getCode();
    }

    @Override
    public UserStat convertToEntityAttribute(Integer code) {
        if(code == null){
            return null;
        }
        return UserStat.getUserStat(code);
    }
}
