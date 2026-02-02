package io.why503.performanceservice.util.mapper;

import io.why503.performanceservice.domain.round.model.dto.request.RoundRequest;
import io.why503.performanceservice.domain.round.model.dto.response.RoundResponse;
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
                .show(showEntity)
                .startDt(request.roundDt())
                .num(calculatedNo)
                .casting(request.roundCast())
                .status(request.roundStatus())
                .build();
    }

    //Entity -> Response
    public RoundResponse entityToDto(RoundEntity entity) {
        return new RoundResponse(
                entity.getSq(),
                entity.getShow().getSq(),
                entity.getStartDt(),
                entity.getNum(),
                entity.getCasting(),
                entity.getStatus().getDescription(),
                entity.getStatus()
        );
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