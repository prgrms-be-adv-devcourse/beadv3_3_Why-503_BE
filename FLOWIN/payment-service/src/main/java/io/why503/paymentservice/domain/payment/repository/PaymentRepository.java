package io.why503.paymentservice.domain.payment.repository;

import io.why503.paymentservice.domain.payment.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * 주문 번호로 결제 내역 조회 (Unique)
     * - 예매(Booking)나 포인트충전(Point)의 orderId와 매핑됩니다.
     * - 결제 승인, 취소, 중복 방지 로직에서 식별자로 사용됩니다.
     */
    Optional<Payment> findByOrderId(String orderId);

    /**
     * 사용자별 결제 이력 조회
     * - 마이페이지 등에서 사용됩니다.
     * - 최신순(createdDt Desc)으로 정렬하여 반환합니다.
     */
    List<Payment> findAllByUserSqOrderByCreatedDtDesc(Long userSq);
}