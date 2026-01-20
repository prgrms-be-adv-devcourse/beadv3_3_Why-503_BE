package io.why503.performanceservice.domain.show.mapper;

import io.why503.performanceservice.domain.show.model.dto.ShowRequest;
import io.why503.performanceservice.domain.show.model.dto.ShowResponse;
import io.why503.performanceservice.domain.show.model.entity.ShowEntity;
import io.why503.performanceservice.domain.show.model.enums.ShowCategory;
import io.why503.performanceservice.domain.show.model.enums.ShowStatus;

import org.springframework.stereotype.Component;

/**
 * ShowMapper
 *
 * 역할:
 * - ShowEntity -> ShowResponse 변환 책임
 *
 * 설계 의도:
 * - Service 로직에서 DTO 변환 코드 제거
 * - 응답 구조 변경 시 Mapper만 수정
 */
@Component
public class ShowMapper {

    public ShowEntity toEntity(ShowRequest req, Long companySq) {
        ShowEntity entity = ShowEntity.builder()
                .showName(req.getShowName())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .openDt(req.getOpenDt())
                .showTime(req.getShowTime())
                .viewingAge(req.getViewingAge())
                .concertHallSq(req.getConcertHallSq())
                .companySq(companySq)
                .build();

        entity.setCategory(ShowCategory.fromCode(req.getCategory()));
        entity.setShowStatus(ShowStatus.SCHEDULED);

        return entity;
    }

    public ShowResponse toResponse(ShowEntity entity) {
        if (entity == null) {
            return null;
        }

        return ShowResponse.builder()
                .showSq(entity.getShowSq())
                .showName(entity.getShowName())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .openDt(entity.getOpenDt())
                .showTime(entity.getShowTime())
                .viewingAge(entity.getViewingAge())
                .category(entity.getCategoryEnum())
                .showStat(entity.getShowStatus())
                .concertHallSq(entity.getConcertHallSq())
                .companySq(entity.getCompanySq())
                .build();
    }
}
