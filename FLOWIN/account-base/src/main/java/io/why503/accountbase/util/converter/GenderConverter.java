package io.why503.accountbase.util.converter;

import io.why503.accountbase.model.enums.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
/*
enum 변환기 ver. Gender
java <-> sql에서 작동함
* */
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
