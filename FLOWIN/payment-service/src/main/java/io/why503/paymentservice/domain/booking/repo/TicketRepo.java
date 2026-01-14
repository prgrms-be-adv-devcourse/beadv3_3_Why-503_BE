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

    // "특정 좌석(showingSeatSq)이면서 + 상태가 목록(statuses) 중 하나라도 겹치면 -> true 반환"
    boolean existsByShowingSeatSqAndTicketStatusIn(Long showingSeatSq, Collection<TicketStatus> statuses);

    //  비관적 락(Pessimistic Lock) 적용
    // - PESSIMISTIC_WRITE: 트랜잭션 종료 시까지 쓰기/읽기 차단 (배타적 락)
    // - 데이터가 없으면 '빈 공간'에 락을 걸어 INSERT를 막음 (Gap Lock)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Ticket t where t.showingSeatSq = :seatSq and t.ticketStatus in :statuses")
    List<Ticket> findWithLockByShowingSeatSqAndTicketStatusIn(
            @Param("seatSq") Long seatSq,
            @Param("statuses") Collection<TicketStatus> statuses
    );
}