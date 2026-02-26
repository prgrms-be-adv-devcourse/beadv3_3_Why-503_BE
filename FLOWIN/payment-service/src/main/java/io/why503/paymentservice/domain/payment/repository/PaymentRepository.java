package io.why503.paymentservice.domain.payment.repository;

import io.why503.paymentservice.domain.payment.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 결제 거래 데이터의 영속성 관리를 담당하는 레포지토리
 * - 주문 식별자 기반의 조회 및 사용자별 이력 추출 기능을 제공
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 외부 시스템과의 승인 결과 대조를 위한 주문 식별자 기반 조회
    Optional<Payment> findByOrderId(String orderId);

    // 사용자의 결제 이력을 최신 순서대로 추출
    List<Payment> findAllByUserSqOrderByCreatedDtDesc(Long userSq);

    // 특정 예매 건에 할당된 결제 정보를 확인하여 환불 절차에 활용
    Optional<Payment> findByBookingSq(Long bookingSq);
}