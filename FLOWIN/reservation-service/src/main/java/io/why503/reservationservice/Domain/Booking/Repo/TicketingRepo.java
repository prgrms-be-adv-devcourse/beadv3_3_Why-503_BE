package io.why503.reservationservice.Domain.Booking.Repo;

import io.why503.reservationservice.Domain.Booking.Model.Ett.Ticketing; // ★ 패키지 수정됨
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketingRepo extends JpaRepository<Ticketing, Long> {
}