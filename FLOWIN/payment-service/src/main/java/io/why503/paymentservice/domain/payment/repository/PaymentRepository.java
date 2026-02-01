package io.why503.paymentservice.domain.payment.repository;

import io.why503.paymentservice.domain.payment.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 결제 내역 데이터에 대한 데이터 액세스 처리를 담당하는 레포지토리
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 주문 번호를 기반으로 결제 내역 조회
    Optional<Payment> findByOrderId(String orderId);

    // 사용자의 전체 결제 이력 목록 조회
    List<Payment> findAllByUserSqOrderByCreatedDtDesc(Long userSq);
}