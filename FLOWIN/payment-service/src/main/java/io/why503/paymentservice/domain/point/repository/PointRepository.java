package io.why503.paymentservice.domain.point.repository;

import io.why503.paymentservice.domain.point.model.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 포인트 충전 데이터에 대한 데이터 액세스 처리를 담당하는 레포지토리
 */
public interface PointRepository extends JpaRepository<Point, Long> {

    Optional<Point> findByOrderId(String orderId);

    List<Point> findAllByUserSqOrderByCreatedDtDesc(Long userSq);
}