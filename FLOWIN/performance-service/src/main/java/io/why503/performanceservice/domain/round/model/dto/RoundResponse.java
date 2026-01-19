package io.why503.performanceservice.domain.round.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoundResponse {
    private Long roundSq;               //회차 ID (PK)
    private Long showSq;                //공연 ID
    private LocalDateTime roundDt;      //회차 일시
    private Integer roundNum;           //회차 번호
    private String roundCast;           //출연진
    private String roundStatusName;     //상태 설명
    private RoundStatus roundStatus;    //상태 Enum

}
