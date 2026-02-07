package io.why503.performanceservice.domain.showseat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import java.util.List;

public interface ShowSeatRepository extends JpaRepository<ShowSeatEntity, Long> {

    List<ShowSeatEntity> findByShow_Sq(Long showSq);

}
