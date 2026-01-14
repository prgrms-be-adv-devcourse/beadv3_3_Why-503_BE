package io.why503.paymentservice.domain.booking.repo;

import io.why503.paymentservice.domain.booking.model.ett.Ticket;
import io.why503.paymentservice.domain.booking.model.type.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface TicketRepo extends JpaRepository<Ticket, Long> {

    /**
     * [변경 사항]
     * 리뷰 피드백 반영: 불필요한 비관적 락(@Lock) 제거
     * 단순 조회용 메서드로 변경
     */
    boolean existsByShowingSeatSqAndTicketStatusIn(Long showingSeatSq, Collection<TicketStatus> statuses);

}