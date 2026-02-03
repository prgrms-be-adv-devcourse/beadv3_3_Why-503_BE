package io.why503.performanceservice.domain.showseat.model.dto.response;

import io.why503.performanceservice.domain.seat.model.dto.response.SeatResponse;
import io.why503.performanceservice.domain.showseat.model.enums.ShowSeatGrade;

public record ShowSeatResponse(
        Long showSeatSq,        // ShowSeat PK
        Long showSq,            // 공연 PK (순환참조 방지용)
        ShowSeatGrade grade,    // 등급
        Long price,              // 가격
        SeatResponse seat       // 물리적 좌석 정보
){}
