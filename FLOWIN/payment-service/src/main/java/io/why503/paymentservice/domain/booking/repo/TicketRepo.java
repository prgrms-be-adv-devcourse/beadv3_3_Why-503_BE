package io.why503.paymentservice.domain.booking.repo;

import io.why503.paymentservice.domain.booking.model.ett.Ticket;
import io.why503.paymentservice.domain.booking.model.type.TicketStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TicketRepo extends JpaRepository<Ticket, Long> {

    /**
     * 단순 조회: 특정 좌석이 지정된 상태 목록에 포함되는지 확인
     * (락을 걸지 않으므로 단순 상태 확인용으로 사용)
     */
    boolean existsByShowingSeatSqAndTicketStatusIn(Long showingSeatSq, Collection<TicketStatus> statuses);

    /**
     * 락 조회: 특정 좌석의 티켓 상태를 확인하며 비관적 락(Exclusive Lock)을 획득
     * - 목적: 중복 예매 방지 (동시성 제어)
     * - 동작: 트랜잭션이 종료될 때까지 해당 데이터에 대한 읽기/쓰기를 차단 (PESSIMISTIC_WRITE)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Ticket t where t.showingSeatSq = :seatSq and t.ticketStatus in :statuses")
    List<Ticket> findWithLockByShowingSeatSqAndTicketStatusIn(
            @Param("seatSq") Long seatSq,
            @Param("statuses") Collection<TicketStatus> statuses
    );
}