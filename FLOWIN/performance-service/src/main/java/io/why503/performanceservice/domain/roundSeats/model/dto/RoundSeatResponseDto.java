package io.why503.performanceservice.domain.roundSeats.model.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RoundSeatResponseDto(
        Long roundSeatSq,                    //회차 좌석 시퀀스
        Long roundSq,                        //회차 시퀀스
        Long showSeatSq,                     //공연 좌석 시퀀스
        RoundSeatStatus roundSeatStatus,     //회차 좌석 상태
        String roundSeatStatusName,          //상태 설명 (판매 가능, 선점됨, 판매 완료, 판매 제한)
        LocalDateTime roundSeatStatusTime    //상태 변경 일시
) {
}
