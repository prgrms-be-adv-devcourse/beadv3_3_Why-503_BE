package io.why503.performanceservice.domain.roundSeats.model.mapper;


import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.roundSeats.model.dto.RoundSeatRequest;
import io.why503.performanceservice.domain.roundSeats.model.dto.RoundSeatResponse;
import io.why503.performanceservice.domain.roundSeats.model.entity.RoundSeatEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RoundSeatMapper {


    //Request -> Entity
    // DB 저장을 위해 RoundEntity 객체를 받음
    public RoundSeatEntity dtoToEntity(RoundSeatRequest request, RoundEntity roundEntity){
        return RoundSeatEntity.builder()
                .roundSq(roundEntity)
                .showSeatSq(request.showSeatSq())
                .roundSeatStatus(request.roundSeatStatus())
                .roundSeatStatusTime(LocalDateTime.now())
                .build();

    }

    //Entity -> Response
    public RoundSeatResponse entityToDto(RoundSeatEntity entity){
        return RoundSeatResponse.builder()
                .roundSeatSq(entity.getRoundSeatSq())
                .roundSq(entity.getRoundSq().getSq())
                .showSeatSq(entity.getShowSeatSq())
                .roundSeatStatus(entity.getRoundSeatStatus())
                .roundSeatStatusName(entity.getRoundSeatStatus().getDescription())
                .roundSeatStatusTime(entity.getRoundSeatStatusTime())
                .build();
    }
}
