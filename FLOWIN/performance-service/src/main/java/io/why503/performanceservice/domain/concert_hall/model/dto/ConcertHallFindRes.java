package io.why503.performanceservice.domain.concert_hall.model.dto;

import io.why503.performanceservice.domain.concert_hall.model.Ett.ConcertHallEtt;

public record ConcertHallFindRes(
        String name,
        int totalSeat
) {
    public static ConcertHallFindRes from(ConcertHallEtt concert) {
        return new ConcertHallFindRes(
                concert.getName(),
                concert.getSeatScale()
        );
    }
}
