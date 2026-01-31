package io.why503.paymentservice.domain.booking.repository;

import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * 주문 번호로 예매 조회
     * - PG 연동 및 상세 조회 시 사용
     * - [최적화] 티켓 목록까지 한 번에 Join 해서 가져옴
     */
    @EntityGraph(attributePaths = "tickets")
    Optional<Booking> findByOrderId(String orderId);

    /**
     * 사용자별 예매 목록 조회
     * - [최적화] 목록 조회 시 N+1 문제 방지 (필수)
     */
    @EntityGraph(attributePaths = "tickets")
    List<Booking> findAllByUserSqOrderByCreatedDtDesc(Long userSq);

    /**
     * 만료된 미결제 예매 조회 (스케줄러용)
     * - 삭제나 취소 처리를 할 것이므로 티켓 정보도 같이 로드하는 것이 좋음
     */
    @EntityGraph(attributePaths = "tickets")
    List<Booking> findAllByStatusAndCreatedDtBefore(BookingStatus status, LocalDateTime criteriaDt);
}