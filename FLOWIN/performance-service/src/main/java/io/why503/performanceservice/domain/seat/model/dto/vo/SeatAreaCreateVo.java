package io.why503.performanceservice.domain.seat.model.dto.vo;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SeatAreaCreateVo(
        @NotBlank String seatArea,  //좌석 구역명 (ex: A, B, VIP
        @NotNull int seatCount      // 해당 구역의 좌석 개수
){ }