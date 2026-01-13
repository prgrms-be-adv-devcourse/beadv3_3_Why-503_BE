package io.why503.performanceservice.domain.seat.model.dto;

import io.why503.performanceservice.domain.seat.model.Ett.SeatEtt;

public record SeatFindRes(
        Long id,
        int no,
        String area,
        int area_no,
        Long concertHallEtt

) {
    public static SeatFindRes from(SeatEtt seat) {
        return new SeatFindRes(
                seat.getId(),
                seat.getSeatNo(),
                seat.getSeatArea(),
                seat.getAreaSeatNo(),
                seat.getConcertHallEtt().getId()
        );
    }
}