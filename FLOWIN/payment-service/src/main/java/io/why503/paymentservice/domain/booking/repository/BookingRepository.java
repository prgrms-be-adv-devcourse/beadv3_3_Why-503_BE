package io.why503.paymentservice.domain.booking.repository;

import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 예매 엔티티에 대한 데이터 액세스 처리를 담당하는 레포지토리
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = "tickets")
    Optional<Booking> findByOrderId(String orderId);

    @EntityGraph(attributePaths = "tickets")
    List<Booking> findAllByUserSqOrderByCreatedDtDesc(Long userSq);

    @EntityGraph(attributePaths = "tickets")
    List<Booking> findAllByStatusAndCreatedDtBefore(BookingStatus status, LocalDateTime dateTime);
}