package io.why503.performanceservice.domain.concert_hall.model.dto;

import jakarta.validation.constraints.NotBlank;

public record ConcertHallRegisterRes(
        @NotBlank(message = " 콘서트 id값 필요")
        Long id,
        @NotBlank(message = "이름값 필요")
        String name,
        @NotBlank(message = "좌석수 필요")
        int seatScale
) {

}
