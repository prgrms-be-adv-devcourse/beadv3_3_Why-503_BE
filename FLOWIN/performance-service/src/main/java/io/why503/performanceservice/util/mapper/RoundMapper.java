package io.why503.performanceservice.util.mapper;

import io.why503.performanceservice.domain.round.model.dto.RoundRequest;
import io.why503.performanceservice.domain.round.model.dto.RoundResponse;
import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.show.model.entity.ShowEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RoundMapper {

    //Request -> Entity
    public RoundEntity dtoToEntity(RoundRequest request, ShowEntity showEntity, Integer calculatedNo) {
        return RoundEntity.builder()
                .showSq(showEntity)
                .roundDt(request.roundDt())
                .roundNum(calculatedNo)
                .roundCast(request.roundCast())
                .roundStatus(request.roundStatus())
                .build();
    }

    //Entity -> Response
    public RoundResponse entityToDto(RoundEntity entity) {
        return RoundResponse.builder()
                .roundSq(entity.getRoundSq())
                .showSq(entity.getShowSq().getShowSq())
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