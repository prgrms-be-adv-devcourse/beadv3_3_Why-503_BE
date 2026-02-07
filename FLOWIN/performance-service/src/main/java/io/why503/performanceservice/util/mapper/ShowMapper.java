package io.why503.performanceservice.util.mapper;

import io.why503.performanceservice.domain.hall.model.entity.HallEntity;
import io.why503.performanceservice.domain.show.model.dto.request.ShowRequest;
import io.why503.performanceservice.domain.show.model.dto.response.ShowResponse;
import io.why503.performanceservice.domain.show.model.entity.ShowEntity;
import io.why503.performanceservice.domain.show.model.enums.ShowCategory;
import io.why503.performanceservice.domain.show.model.enums.ShowGenre;
import io.why503.performanceservice.domain.show.model.enums.ShowStatus;
import org.springframework.stereotype.Component;

@Component
public class ShowMapper {

    public ShowEntity requestToEntity(ShowRequest request, Long companySq, HallEntity hallEntity) {
        return ShowEntity.builder()
                .name(request.showName())
                .startDt(request.showStartDt())
                .endDt(request.showEndDt())
                .openDt(request.showOpenDt())
                .runningTime(request.showRunningTime())
                .viewingAge(request.showViewingAge())
                .hall(hallEntity)
                .category(ShowCategory.fromCode(request.showCategory()))
                .genre(ShowGenre.fromCode(request.showGenre()))
                .companySq(companySq)
                .status(ShowStatus.fromCode(request.showStatus()))
                .build();
    }

    public ShowResponse entityToResponse(ShowEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ShowResponse(
                entity.getSq(),
                entity.getName(),
                entity.getStartDt(),
                entity.getEndDt(),
                entity.getOpenDt(),
                entity.getRunningTime(),
                entity.getViewingAge(),
                entity.getCategory(),
                entity.getGenre(),
                entity.getStatus(),
                entity.getHall().getSq(),
                entity.getCompanySq()
        );
    }
}
