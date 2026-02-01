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
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * 포인트 충전 요청 생성 및 상태 관리를 담당하는 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;
    private final PointMapper pointMapper;

    // 초기 충전 대기 상태의 포인트 엔티티 생성 및 저장
    @Override
    @Transactional
    public PointResponse createPointCharge(Long userSq, PointRequest request) {
        String orderId = "POINT-" + UUID.randomUUID();

        Point point = Point.builder()
                .userSq(userSq)
                .orderId(orderId)
                .chargeAmount(request.chargeAmount())
                .build();

        Point savedPoint = pointRepository.save(point);
        return pointMapper.entityToResponse(savedPoint);
    }

    // 특정 포인트 충전 요청 건 상세 조회
    @Override
    public PointResponse findPoint(Long userSq, Long pointSq) {
        Point point = pointRepository.findById(pointSq)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 충전 요청입니다."));

        if (!point.getUserSq().equals(userSq)) {
            throw new SecurityException("본인의 충전 내역만 조회할 수 있습니다.");
        }

        return pointMapper.entityToResponse(point);
    }

    // 사용자의 전체 포인트 충전 이력 목록 조회
    @Override
    public List<PointResponse> findPointsByUser(Long userSq) {
        List<Point> points = pointRepository.findAllByUserSqOrderByCreatedDtDesc(userSq);

        return points.stream()
                .map(point -> pointMapper.entityToResponse(point))
                .toList();
    }

    // 대기 상태인 포인트 충전 요청 취소 처리
    @Override
    @Transactional
    public PointResponse cancelPoint(Long userSq, Long pointSq) {
        Point point = pointRepository.findById(pointSq)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 충전 요청입니다."));

        if (!point.getUserSq().equals(userSq)) {
            throw new SecurityException("본인의 충전 요청만 취소할 수 있습니다.");
        }

        point.cancel();
        return pointMapper.entityToResponse(point);
    }

    // 주문 번호를 기반으로 포인트 엔티티 조회
    @Override
    public Point findByOrderId(String orderId) {
        return pointRepository.findByOrderId(orderId)
                .orElse(null);
    }
}