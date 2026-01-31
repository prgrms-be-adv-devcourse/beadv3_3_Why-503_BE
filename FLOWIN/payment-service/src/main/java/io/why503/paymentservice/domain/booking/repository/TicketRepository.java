package io.why503.paymentservice.domain.booking.repository;

import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import io.why503.paymentservice.domain.booking.model.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * 특정 예매(Booking)에 속한 티켓 목록 조회
     */
    List<Ticket> findAllByBooking(Booking booking);

    /**
     * UUID로 티켓 조회 (입장 확인, QR 검증용)
     */
    Optional<Ticket> findByUuid(String uuid);

    /**
     * 좌석 선점 여부 확인 (중복 예매 방지)
     * - 입력받은 좌석 ID들(roundSeatSqs) 중에서
     * - 특정 상태(statuses: RESERVED, PAID 등)에 해당하는 티켓이 있는지 조회합니다.
     */
    @Query("SELECT t FROM Ticket t WHERE t.roundSeatSq IN :roundSeatSqs AND t.status IN :statuses")
    List<Ticket> findAllByRoundSeatSqInAndStatusIn(
            @Param("roundSeatSqs") List<Long> roundSeatSqs,
            @Param("statuses") List<TicketStatus> statuses
    );
}