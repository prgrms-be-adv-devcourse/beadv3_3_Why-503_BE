package io.why503.accountservice.Model.Enum.EnumConverter;

import io.why503.accountservice.Model.Enum.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class GenderConverter implements AttributeConverter<Gender, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Gender gender) {
        if(gender == null){
            return null;
        }
        return gender.getCode();
    }

    @Override
    public Gender convertToEntityAttribute(Integer code) {
        if(code == null){
            return null;
        }
        return Gender.getGender(code);
    }
}
