package io.why503.performanceservice.domain.seat.sv;

import io.why503.performanceservice.domain.concert_hall.model.Ett.ConcertHallEtt;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public interface SeatSv {
    Optional create(ConcertHallEtt concertHall);
    Optional adjust(ConcertHallEtt concertHall, int newSeatScale);
    boolean exists(ConcertHallEtt concertHall, String seatArea, int seatNo);


}
