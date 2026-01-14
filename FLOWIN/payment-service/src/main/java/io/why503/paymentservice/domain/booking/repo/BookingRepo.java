package io.why503.paymentservice.domain.booking.repo;

import io.why503.paymentservice.domain.booking.model.ett.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

// Booking 엔티티용 JPA 리포지토리
public interface BookingRepo extends JpaRepository<Booking, Long> {

    // [리뷰 반영] N+1 문제 해결을 위한 Fetch Join 쿼리
    // Booking을 조회할 때 연관된 Tickets까지 한 방에 가져옵니다.
    // DISTINCT: 1:N 조인 시 Booking 데이터 뻥튀기 방지 (JPA 엔티티 중복 제거)
    @Query("SELECT DISTINCT b FROM Booking b JOIN FETCH b.tickets WHERE b.bookingSq = :bookingSq")
    Optional<Booking> findByIdWithTickets(@Param("bookingSq") Long bookingSq);
}
