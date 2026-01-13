package io.why503.performanceservice.domain.concert_hall.model.dto;

import io.why503.performanceservice.domain.concert_hall.model.Ett.ConcertHallEtt;
import jakarta.validation.constraints.NotBlank;

public record ConcertHallUpdateRes(
        @NotBlank(message = "이름 변경 필요")
        String name
) {

    public static ConcertHallUpdateRes from(
            ConcertHallEtt concert) {
        return new ConcertHallUpdateRes(
                concert.getName()
        );
    }
}
