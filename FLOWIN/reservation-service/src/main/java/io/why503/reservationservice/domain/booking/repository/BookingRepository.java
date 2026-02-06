package io.why503.reservationservice.domain.booking.repository;

import io.why503.reservationservice.domain.booking.model.entity.Booking;
import io.why503.reservationservice.domain.booking.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 예매(Booking) 엔티티에 대한 데이터 액세스를 담당하는 인터페이스
 * - 핵심 역할: 주문 번호 조회, 사용자별 목록 조회, 좌석 중복 선점 체크
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * 주문 번호(OrderId)로 예매 건 조회
     * - 결제 서비스에서 결제 승인/취소 요청 시 식별자로 사용
     */
    Optional<Booking> findByOrderId(String orderId);

    /**
     * 특정 사용자의 예매 목록 조회 (최신순)
     * - 마이페이지 등에서 사용
     */
    List<Booking> findAllByUserSqOrderByCreatedDtDesc(Long userSq);

    /**
     * 좌석 선점 여부 확인 (중복 예매 방지)
     * - 요청한 좌석(roundSeatSqs) 중 하나라도 '취소되지 않은(Active)' 예매에 포함되어 있는지 확인
     * - PENDING(대기) 상태여도 선점된 것으로 간주함
     */
    @Query("SELECT DISTINCT b FROM Booking b " +
            "JOIN b.bookingSeats bs " +
            "WHERE bs.roundSeatSq IN :roundSeatSqs " +
            "AND b.status IN :activeStatuses")
    List<Booking> findConflictingBookings(
            @Param("roundSeatSqs") List<Long> roundSeatSqs,
            @Param("activeStatuses") List<BookingStatus> activeStatuses
    );
}