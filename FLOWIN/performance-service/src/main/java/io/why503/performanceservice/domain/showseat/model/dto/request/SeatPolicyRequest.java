package io.why503.performanceservice.domain.showseat.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SeatPolicyRequest (
        @NotBlank String seatArea,  //좌석 구역(예: VIP, A, B)
        @NotBlank String grade,     //공연 좌석 등급
        @NotNull int price          //공연 좌석 가격
){ }
