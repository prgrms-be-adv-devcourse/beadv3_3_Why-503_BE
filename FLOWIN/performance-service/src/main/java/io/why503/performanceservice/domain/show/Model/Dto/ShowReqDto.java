package io.why503.performanceservice.domain.show.Model.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ShowReqDto {

    private String showName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime openDt;
    private String showTime;
    private String viewingAge;

    // 번호로 받음
    private int category;

    private Long concertHallSq;
    private Long companySq;
}
