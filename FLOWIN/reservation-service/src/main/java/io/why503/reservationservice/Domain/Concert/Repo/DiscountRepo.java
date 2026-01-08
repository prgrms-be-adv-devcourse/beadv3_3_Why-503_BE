package io.why503.reservationservice.Domain.Concert.Repo;

import io.why503.reservationservice.Domain.Concert.Model.Ett.Discount; // ett 패키지 경로 확인
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepo extends JpaRepository<Discount, Long> {
}
