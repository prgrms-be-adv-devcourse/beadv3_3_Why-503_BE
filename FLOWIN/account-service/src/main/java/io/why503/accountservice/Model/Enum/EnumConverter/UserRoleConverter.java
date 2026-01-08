package io.why503.accountservice.Model.Enum.EnumConverter;

import io.why503.accountservice.Model.Enum.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

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
