package io.why503.accountservice.domain.account.model.dto.enumconverter;

import io.why503.accountservice.domain.account.model.dto.UserStat;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
/*
enum 변환기 vec.UserStat
java -> sql에서 작동함
* */
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
