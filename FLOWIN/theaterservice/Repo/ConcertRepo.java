package io.why503.theaterservice.Repo;

import io.why503.theaterservice.model.Ett.concert_ItemEtt;
import io.why503.theaterservice.model.Dto.hallRes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface concertRepo extends JpaRepository<concert_ItemEtt, Long> {
    hallRes findByStat(concert_ItemEtt concert_ItemEtt);
}
