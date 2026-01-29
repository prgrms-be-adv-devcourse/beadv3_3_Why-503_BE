package io.why503.paymentservice.domain.booking.repository;

import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 티켓 레포지토리
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // UUID로 티켓 조회
    Optional<Ticket> findByUuid(String uuid);
}