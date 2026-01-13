package io.why503.performanceservice.domain.show.Model.Dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import io.why503.performanceservice.domain.show.Model.Enum.ShowCategory;
import io.why503.performanceservice.domain.show.Model.Enum.ShowStatus;

@Getter
@Builder
public class ShowResDto {

    private Long showSq;
    private String showName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime openDt;
    private String showTime;
    private String viewingAge;
    private ShowCategory category;
    private ShowStatus showStat;
    private Long concertHallSq;
    private Long companySq;
}
