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

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 상세 조회: 티켓 정보도 같이 로딩합니다. (DB 성능 최적화)
    @EntityGraph(attributePaths = {"tickets"})
    Optional<Booking> findByBookingSq(Long bookingSq);

    // 스케줄러용: "돈 안 내고 시간만 끄는(PENDING + 시간초과)" 예매를 찾습니다.
    @Query("SELECT b FROM Booking b JOIN FETCH b.tickets " +
            "WHERE b.bookingStatus = :status AND b.bookingDt < :dateTime")
    List<Booking> findExpired(@Param("status") BookingStatus status,
                              @Param("dateTime") LocalDateTime dateTime);
}