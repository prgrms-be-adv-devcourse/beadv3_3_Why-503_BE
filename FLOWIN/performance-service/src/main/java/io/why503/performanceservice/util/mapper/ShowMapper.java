package io.why503.performanceservice.util.mapper;

import io.why503.performanceservice.domain.hall.model.entity.HallEntity;
import io.why503.performanceservice.domain.show.model.dto.request.ShowRequest;
import io.why503.performanceservice.domain.show.model.dto.response.ShowResponse;
import io.why503.performanceservice.domain.show.model.entity.ShowEntity;

import io.why503.performanceservice.domain.show.model.enums.ShowStatus;
import org.springframework.stereotype.Component;

/**
 * ShowMapper
 * 역할:
 * - ShowEntity -> ShowResponse 변환 책임
 * 설계 의도:
 * - Service 로직에서 DTO 변환 코드 제거
 * - 응답 구조 변경 시 Mapper만 수정
 */
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
                .category(request.showCategory())
                .companySq(companySq)
                .status(ShowStatus.valueOf(request.showStatus()))
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
                entity.getStatus(),
                entity.getHall().getSq(),
                entity.getCompanySq()
        );
    }
}
