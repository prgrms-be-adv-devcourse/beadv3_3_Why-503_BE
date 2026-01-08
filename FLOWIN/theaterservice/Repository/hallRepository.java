package io.why503.theaterservice.Repository;

import io.why503.theaterservice.model.Entity.concert_hall;
import io.why503.theaterservice.model.dto.hallResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface hallRepository extends JpaRepository<concert_hall, Long> {
    hallResponse findById(concert_hall concert);
}
