package io.why503.performanceservice.domain.round.mapper;

import io.why503.performanceservice.domain.round.model.dto.RoundRequest;
import io.why503.performanceservice.domain.round.model.dto.RoundResponse;
import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RoundMapper {

    //Request -> Entity
    public RoundEntity dtoToEntity(RoundRequest request, Integer calculatedNo) {
        return RoundEntity.builder()
                .showSq(request.getShowSq())
                .roundDt(request.getRoundDt())
                .roundNum(calculatedNo)
                .roundCast(request.getRoundCast())
                .roundStatus(request.getRoundStatus())
                .build();
    }

    //Entity -> Response
    public RoundResponse entityToDto(RoundEntity entity) {
        return RoundResponse.builder()
                .roundSq(entity.getRoundSq())
                .showSq(entity.getShowSq())
                .roundDt(entity.getRoundDt())
                .roundNum(entity.getRoundNum())
                .roundCast(entity.getRoundCast())
                .roundStatus(entity.getRoundStatus())
                .roundStatusName(entity.getRoundStatus().getDescription())
                .build();
    }
    //Entity -> Dto
    public List<RoundResponse> entityListToDtoList(List<RoundEntity> entities) {
        List<RoundResponse> dtoList = new ArrayList<>();

        for (RoundEntity entity : entities) {
            dtoList.add(entityToDto(entity));
        }
        return dtoList;
    }
}