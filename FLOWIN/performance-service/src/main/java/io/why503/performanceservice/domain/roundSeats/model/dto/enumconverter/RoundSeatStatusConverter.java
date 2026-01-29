package io.why503.performanceservice.domain.roundSeats.model.dto.enumconverter;

import io.why503.performanceservice.domain.roundSeats.model.enums.RoundSeatStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public class RoundSeatStatusConverter implements AttributeConverter<RoundSeatStatus, Integer> {

    //자바Enum -> DB(숫자) 변환 :저장
    @Override
    public Integer convertToDatabaseColumn(RoundSeatStatus roundSeatStatus) {

        if(roundSeatStatus == null){
            return null;
        }
        return roundSeatStatus.getDbCode();

    }


    //DB(숫자) -> 자바(Enum) 변환 :조회
    @Override
    public RoundSeatStatus convertToEntityAttribute(Integer dbData) {
        if(dbData == null){
            return null;
        }
        for (RoundSeatStatus status : RoundSeatStatus.values()){
            if(status.getDbCode().equals(dbData)){
                return status;
            }
        }
        throw new IllegalArgumentException("알 수 없는 DB 데이터 입니다: "+dbData);

    }
}
