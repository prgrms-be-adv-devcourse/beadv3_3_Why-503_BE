package io.why503.paymentservice.domain.booking.repository;

import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * [QR 입장 확인용]
     * UUID 문자열로 티켓 엔티티를 찾습니다.
     * select * from ticket where ticket_uuid = ?
     */
    Optional<Ticket> findByTicketUuid(String ticketUuid);
}