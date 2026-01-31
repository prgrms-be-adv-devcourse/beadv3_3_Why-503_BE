package io.why503.paymentservice.domain.point.repository;

import io.why503.paymentservice.domain.point.model.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {

    /**
     * 주문 번호로 충전 요청 조회 (Unique Key)
     * - PG 결제 승인/취소 시 식별자로 사용됩니다.
     */
    Optional<Point> findByOrderId(String orderId);

    /**
     * 사용자별 충전 이력 조회
     * - 마이페이지 등에서 사용됩니다.
     * - 최신순(createdDt Desc)으로 정렬하여 반환합니다.
     */
    List<Point> findAllByUserSqOrderByCreatedDtDesc(Long userSq);
}