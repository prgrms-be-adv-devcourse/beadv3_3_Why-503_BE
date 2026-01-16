package io.why503.accountservice.domain.accounts.utils.converter;

import io.why503.accountservice.domain.accounts.model.enums.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
/*
enum 변환기 ver.UserRole
java <-> sql에서 작동함
* */
@Converter(autoApply = false)
public class UserRoleConverter implements AttributeConverter<UserRole, Integer> {
    @Override
    public Integer convertToDatabaseColumn(UserRole userRole) {
        if(userRole == null){
            return null;
        }
        return userRole.getCode();
    }

    @Override
    public UserRole convertToEntityAttribute(Integer code) {
        if(code == null){
            return null;
        }
        return UserRole.getUserRole(code);
    }
}
