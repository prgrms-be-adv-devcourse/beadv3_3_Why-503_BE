package io.why503.performanceservice.domain.seat.repo;

import io.why503.performanceservice.domain.concert_hall.model.Ett.ConcertHallEtt;
import io.why503.performanceservice.domain.seat.model.Ett.SeatEtt;
import org.springframework.data.jpa.repository.JpaRepository;\
import java.util.List;


public interface SeatRepo extends JpaRepository<SeatEtt, Long> {

    int count(ConcertHallEtt concertHall);

    int MaxSeatNo(ConcertHallEtt concertHall);

    List<SeatEtt> findSeats(ConcertHallEtt concertHall, int limit);

    boolean exists(ConcertHallEtt concertHall, String seatArea, int seatNo);

}
