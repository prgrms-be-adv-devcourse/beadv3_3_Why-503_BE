package io.why503.paymentservice.domain.ticketing.repo;

import io.why503.paymentservice.domain.ticketing.model.ett.Ticketing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketingRepo extends JpaRepository<Ticketing, Long> {
    // 특정 유저의 예매 내역 조회
    List<Ticketing> findByUserSq(Long userSq);
}