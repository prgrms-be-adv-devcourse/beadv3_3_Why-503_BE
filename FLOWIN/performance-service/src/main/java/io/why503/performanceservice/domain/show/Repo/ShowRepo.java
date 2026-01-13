package io.why503.performanceservice.domain.show.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.why503.performanceservice.domain.show.Model.Ett.ShowEtt;

@Repository
public interface ShowRepo extends JpaRepository<ShowEtt, Long> {
}
