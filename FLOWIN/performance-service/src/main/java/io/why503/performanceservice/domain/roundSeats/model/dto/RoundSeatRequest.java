package io.why503.performanceservice.domain.roundSeats.model.dto;

import jakarta.validation.constraints.NotNull;

public record RoundSeatRequest(

        @NotNull(message = "회차 정보는 필수입니다.")
        Long roundSq,                      //회차 시퀀스

        @NotNull(message = "공연 좌석 정보는 필수입니다.")
        Long showSeatSq,                   //공연 좌석 시퀀스

        @NotNull(message = "회차 상태 정보는 필수입니다.")
        RoundSeatStatus roundSeatStatus    //생성,수정 요청 시 희망하는 좌석 상태

){
}
