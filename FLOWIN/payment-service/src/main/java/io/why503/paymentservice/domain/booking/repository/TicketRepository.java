package io.why503.paymentservice.domain.booking.repository;

import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 티켓 레포지토리
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * UUID로 티켓 조회
     * - QR 코드 입장 확인 시 사용됩니다.
     */
    Optional<Ticket> findByTicketUuid(String ticketUuid);
}