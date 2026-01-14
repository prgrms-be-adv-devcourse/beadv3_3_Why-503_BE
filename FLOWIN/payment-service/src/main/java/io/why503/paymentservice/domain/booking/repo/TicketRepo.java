package io.why503.paymentservice.domain.booking.repo;

import io.why503.paymentservice.domain.booking.model.ett.Ticket;
import io.why503.paymentservice.domain.booking.model.type.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface TicketRepo extends JpaRepository<Ticket, Long> {

    // "특정 좌석(showingSeatSq)이면서 + 상태가 목록(statuses) 중 하나라도 겹치면 -> true 반환"
    boolean existsByShowingSeatSqAndTicketStatusIn(Long showingSeatSq, Collection<TicketStatus> statuses);
}