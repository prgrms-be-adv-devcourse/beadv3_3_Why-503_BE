package io.why503.performanceservice.domain.round.model.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RoundRequest {
    private Long showSq;         //공연 ID
    private LocalDateTime roundDt;    //회차 일시
    private String roundCast;         //출연진
    private RoundStatus roundStatus;    //상태(예매가능 등)


}
