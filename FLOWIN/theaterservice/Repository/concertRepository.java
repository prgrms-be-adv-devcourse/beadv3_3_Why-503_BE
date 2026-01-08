package io.why503.theaterservice.Repository;

import io.why503.theaterservice.model.Entity.concert_Item;
import io.why503.theaterservice.model.dto.hallResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface concertRepository extends JpaRepository<concert_Item, Long> {
    hallResponse findByStat(concert_Item concert_Item);
}
