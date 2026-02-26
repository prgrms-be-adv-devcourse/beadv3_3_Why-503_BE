package io.why503.reservationservice.domain.booking.repository;

import io.why503.reservationservice.domain.booking.model.entity.Booking;
import io.why503.reservationservice.domain.booking.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 예매 정보에 대한 데이터베이스 접근 및 쿼리 처리를 담당
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 외부 시스템과의 연동 및 결제 정보 대조를 위한 조회
    Optional<Booking> findByOrderId(String orderId);

    // 사용자의 예매 이력을 시간 역순으로 추출
    List<Booking> findAllByUserSqOrderByCreatedDtDesc(Long userSq);

    // 특정 좌석들의 예약 가능 여부를 확인하기 위해 유효한 선점 내역 조회
    @Query("SELECT DISTINCT b FROM Booking b " +
            "JOIN b.bookingSeats bs " +
            "WHERE bs.roundSeatSq IN :roundSeatSqs " +
            "AND b.status IN :activeStatuses")
    List<Booking> findConflictingBookings(
            @Param("roundSeatSqs") List<Long> roundSeatSqs,
            @Param("activeStatuses") List<BookingStatus> activeStatuses
    );
}