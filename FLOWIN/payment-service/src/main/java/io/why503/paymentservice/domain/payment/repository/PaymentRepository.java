package io.why503.paymentservice.domain.payment.repository;

import io.why503.paymentservice.domain.payment.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 결제 내역(Payment) 엔티티에 대한 데이터 액세스를 담당하는 레포지토리
 * - [SQL 동기화] order_id를 통한 단건 조회 및 사용자별 내역 조회를 지원함
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * 주문 번호(order_id)로 결제 내역 조회
     * - PG 승인 프로세스에서 결제 정보를 찾기 위해 필수
     */
    Optional<Payment> findByOrderId(String orderId);

    /**
     * 특정 사용자의 전체 결제 내역 조회
     * - 마이페이지 등에서 결제 이력 노출용
     */
    List<Payment> findAllByUserSqOrderByCreatedDtDesc(Long userSq);

    /**
     * 특정 예매(booking_sq)와 연관된 결제 정보 조회
     * - 환불 시 예매 정보를 기반으로 결제 건을 찾을 때 사용
     */
    Optional<Payment> findByBookingSq(Long bookingSq);
}