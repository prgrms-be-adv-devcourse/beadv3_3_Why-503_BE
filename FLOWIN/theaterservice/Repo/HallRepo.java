package io.why503.theaterservice.Repo;

import io.why503.theaterservice.model.Ett.concert_hallEtt;
import io.why503.theaterservice.model.Dto.hallRes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface hallRepo extends JpaRepository<concert_hallEtt, Long> {
    hallRes findById(concert_hallEtt concert);
}
