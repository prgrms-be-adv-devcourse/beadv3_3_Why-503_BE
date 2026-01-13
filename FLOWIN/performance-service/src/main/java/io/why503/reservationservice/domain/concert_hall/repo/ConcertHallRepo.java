package io.why503.performanceservice.domain.concert_hall.repo;

import io.why503.performanceservice.domain.concert_hall.model.Ett.ConcertHallEtt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertHallRepo extends JpaRepository<ConcertHallEtt, Long> {
    List<ConcertHallEtt> findByStat(String stat);
}
