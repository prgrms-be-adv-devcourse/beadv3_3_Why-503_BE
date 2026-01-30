package io.why503.performanceservice.util.mapper;


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
                .round(roundEntity)
                .showSeatSq(request.showSeatSq())
                .status(request.roundSeatStatus())
                .statusTime(LocalDateTime.now())
                .build();

    }

    //Entity -> Response
    public RoundSeatResponse entityToDto(RoundSeatEntity entity){
        return RoundSeatResponse.builder()
                .roundSeatSq(entity.getSq())
                .roundSq(entity.getRound().getSq())
                .showSeatSq(entity.getShowSeatSq())
                .roundSeatStatus(entity.getStatus())
                .roundSeatStatusName(entity.getStatus().getDescription())
                .roundSeatStatusTime(entity.getStatusTime())
                .build();
    }
}
