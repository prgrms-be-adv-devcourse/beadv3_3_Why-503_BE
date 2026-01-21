package io.why503.paymentservice.domain.booking.repository;

import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.vo.BookingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 예매 레포지토리
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * 예매 상세 조회
     * - N+1 문제 방지를 위해 티켓 목록을 Fetch Join(EntityGraph)으로 함께 가져옵니다.
     */
    @EntityGraph(attributePaths = {"tickets"})
    Optional<Booking> findByBookingSq(Long bookingSq);

    /**
     * 만료된 예매 조회 (스케줄러용)
     * - 특정 상태(PENDING)이면서 지정된 시간 이전에 생성된 건을 찾습니다.
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.tickets " +
            "WHERE b.bookingStatus = :status AND b.bookingDt < :dateTime")
    List<Booking> findExpired(@Param("status") BookingStatus status,
                              @Param("dateTime") LocalDateTime dateTime);

    /**
     * 사용자별 예매 목록 조회
     */
    @Query("SELECT DISTINCT b FROM Booking b JOIN FETCH b.tickets " +
            "WHERE b.userSq = :userSq ORDER BY b.bookingDt DESC")
    List<Booking> findByUserSq(@Param("userSq") Long userSq);

    /**
     * 주문 ID로 조회 (결제 검증용)
     */
    Optional<Booking> findByOrderId(String orderId);
}