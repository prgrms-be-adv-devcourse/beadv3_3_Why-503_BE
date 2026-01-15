package io.why503.performanceservice.domain.showing.model.dto.enumconverter;

import io.why503.performanceservice.domain.showing.model.dto.ShowingStat;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public class ShowingStatConverter implements AttributeConverter<ShowingStat,Integer> {

    //자바Enum -> DB(숫자) 변환 :저장
    @Override
    public Integer convertToDatabaseColumn(ShowingStat attribute) {

        if(attribute == null){
            return null; //값이 없으면 DB에도 null 저장 -> 디버깅시 사용
        }
        return attribute.getDbCode(); //Enum 안의 숫자 반환

    }

    //DB(숫자) -> 자바(Enum) 변환 :조회
    @Override
    public ShowingStat convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }

        //ShowingStat의 모든 Enum을 하나씩 꺼내서 검사
        for(ShowingStat stat: ShowingStat.values()){
            if(stat.getDbCode().equals(dbData)){
                return stat; //Enum 반환
            }
        }

        //DB에 이상한 숫자가 들어있을 경우 에러 처리
        throw new IllegalArgumentException("알 수 없는 DB 데이터입니다: "+ dbData);


    }
}
