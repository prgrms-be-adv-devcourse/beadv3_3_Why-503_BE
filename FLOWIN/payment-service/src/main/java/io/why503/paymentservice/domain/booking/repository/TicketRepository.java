package io.why503.paymentservice.domain.booking.repository;

import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import io.why503.paymentservice.domain.booking.model.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 티켓 엔티티에 대한 데이터 액세스 처리를 담당하는 레포지토리
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByBooking(Booking booking);

    Optional<Ticket> findByUuid(String uuid);

    // 지정된 좌석 ID 목록과 상태에 해당하는 티켓 존재 여부 조회
    @Query("SELECT t FROM Ticket t WHERE t.roundSeatSq IN :roundSeatSqs AND t.status IN :statuses")
    List<Ticket> findAllByRoundSeatSqInAndStatusIn(
            @Param("roundSeatSqs") List<Long> roundSeatSqs,
            @Param("statuses") List<TicketStatus> statuses
    );
}