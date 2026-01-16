package io.why503.performanceservice.domain.roundSeats.model.mapper;


import io.why503.performanceservice.domain.roundSeats.model.dto.RoundSeatRequestDto;
import io.why503.performanceservice.domain.roundSeats.model.dto.RoundSeatResponseDto;
import io.why503.performanceservice.domain.roundSeats.model.entity.RoundSeatEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RoundSeatMapper {


    //Request -> Entity
    public RoundSeatEntity dtoToEntity(RoundSeatRequestDto request){
        return RoundSeatEntity.builder()
                .roundSq(request.roundSq())
                .showSeatSq(request.showSeatSq())
                .roundSeatStatus(request.roundSeatStatus())
                .roundSeatStatusTime(LocalDateTime.now())
                .build();

    }

    //Entity -> Response
    public RoundSeatResponseDto entityToDto(RoundSeatEntity entity){
        return RoundSeatResponseDto.builder()
                .roundSeatSq(entity.getRoundSeatSq())
                .roundSq(entity.getRoundSq())
                .showSeatSq(entity.getShowSeatSq())
                .roundSeatStatus(entity.getRoundSeatStatus())
                .roundSeatStatusName(entity.getRoundSeatStatus().getDescription())
                .roundSeatStatusTime(entity.getRoundSeatStatusTime())
                .build();
    }
}
