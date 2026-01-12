package io.why503.paymentservice.domain.booking.repo;

import io.why503.paymentservice.domain.booking.model.ett.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

// Booking 엔티티용 JPA 리포지토리
public interface BookingRepo extends JpaRepository<Booking, Long> {
}