package io.why503.paymentservice.domain.booking.repo;

import io.why503.paymentservice.domain.booking.model.ett.Booking;
import io.why503.paymentservice.domain.booking.model.vo.BookingStat;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepo extends JpaRepository<Booking, Long> {

    /**
     * 예매 상세 조회 (Fetch Join)
     * - 연관된 Ticket 목록을 함께 조회하여 N+1 문제를 방지합니다.
     * - DISTINCT: 1:N 조인으로 인한 Booking 엔티티 중복을 제거합니다.
     */
    @EntityGraph(attributePaths = {"tickets"})
    Optional<Booking> findByBookingSq(Long bookingSq);

    List<Booking> findExpired(BookingStat status, LocalDateTime dateTime);
}
