package io.why503.paymentservice.domain.point.service.impl;

import io.why503.paymentservice.domain.point.mapper.PointMapper;
import io.why503.paymentservice.domain.point.model.dto.request.PointRequest;
import io.why503.paymentservice.domain.point.model.dto.response.PointResponse;
import io.why503.paymentservice.domain.point.model.entity.Point;
import io.why503.paymentservice.domain.point.repository.PointRepository;
import io.why503.paymentservice.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;
    private final PointMapper pointMapper;

    /**
     * 포인트 충전 요청 생성
     * - '충전 대기' 상태의 Point 엔티티를 생성합니다.
     * - 실제 포인트 증가(Account)는 PG 결제 성공 후 PaymentService에서 처리됩니다.
     */
    @Override
    @Transactional
    public PointResponse createPointCharge(Long userSq, PointRequest request) {
        // 1. 요청 검증 (해피 패스 금지)
        if (request.chargeAmount() == null || request.chargeAmount() <= 0) {
            throw new IllegalArgumentException("충전 금액은 양수여야 합니다.");
        }

        // 2. 주문 번호 생성 (Point 전용 접두사 PT-)
        String orderId = "POINT-" + UUID.randomUUID().toString();

        // 3. 엔티티 생성 및 저장
        Point point = Point.builder()
                .userSq(userSq)
                .orderId(orderId)
                .chargeAmount(request.chargeAmount())
                .build(); // status는 builder 기본값에 의해 READY

        Point savedPoint = pointRepository.save(point);

        return pointMapper.entityToResponse(savedPoint);
    }

    /**
     * 포인트 충전 요청 상세 조회
     */
    @Override
    public PointResponse findPoint(Long userSq, Long pointSq) {
        Point point = pointRepository.findById(pointSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 충전 요청입니다."));

        // 본인 확인
        if (!point.getUserSq().equals(userSq)) {
            throw new IllegalArgumentException("본인의 충전 내역만 조회할 수 있습니다.");
        }

        return pointMapper.entityToResponse(point);
    }

    /**
     * 내 포인트 충전 이력 조회
     */
    @Override
    public List<PointResponse> findPointsByUser(Long userSq) {
        List<Point> points = pointRepository.findAllByUserSqOrderByCreatedDtDesc(userSq);

        // 메서드 참조(::) 금지
        return points.stream()
                .map(point -> pointMapper.entityToResponse(point))
                .toList();
    }

    /**
     * 포인트 충전 취소
     * - 결제 전(READY) 상태인 경우에만 취소 가능
     */
    @Override
    @Transactional
    public PointResponse cancelPoint(Long userSq, Long pointSq) {
        Point point = pointRepository.findById(pointSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 충전 요청입니다."));

        if (!point.getUserSq().equals(userSq)) {
            throw new IllegalArgumentException("본인의 충전 요청만 취소할 수 있습니다.");
        }

        // 엔티티 내부 로직을 통해 상태 검증(READY -> CANCELED)
        point.cancel();

        return pointMapper.entityToResponse(point);
    }

    @Override
    public Point findByOrderId(String orderId) {
        // 규칙: 람다식 사용
        return pointRepository.findByOrderId(orderId)
                .orElse(null);
    }
}