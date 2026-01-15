package io.why503.performanceservice.domain.showing.mapper;

import io.why503.performanceservice.domain.showing.model.dto.ShowingReqDto;
import io.why503.performanceservice.domain.showing.model.dto.ShowingResDto;
import io.why503.performanceservice.domain.showing.model.ett.ShowingEtt;
import org.springframework.stereotype.Component;

@Component
public class ShowingMapper {

    //Req -> Ett
    public ShowingEtt toEtt(ShowingReqDto req, Integer calculatedNo) {
        return ShowingEtt.builder()
                .show(req.getShowSq())
                .dt(req.getDt())
                .no(calculatedNo)
                .cast(req.getCast())
                .stat(req.getStat())
                .build();
    }

    //Ett -> Res
    public ShowingResDto toDto(ShowingEtt ett) {
        return ShowingResDto.builder()
                .sq(ett.getSq())
                .showSq(ett.getShow())
                .dt(ett.getDt())
                .no(ett.getNo())
                .cast(ett.getCast())
                .stat(ett.getStat())
                .statName(ett.getStat().getDescription())
                .build();
    }



}
