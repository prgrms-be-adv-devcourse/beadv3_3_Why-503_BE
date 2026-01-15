package io.why503.performanceservice.domain.showing.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ShowingReqDto {
    private Long showSq;         //공연 ID
    private LocalDateTime dt;    //회차 일시
    private String cast;         //출연진
    private ShowingStat stat;    //상태(예매가능 등)


}
