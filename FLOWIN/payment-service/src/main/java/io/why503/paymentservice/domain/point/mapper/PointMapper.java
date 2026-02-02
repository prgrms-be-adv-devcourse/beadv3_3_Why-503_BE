package io.why503.paymentservice.domain.point.mapper;

import io.why503.paymentservice.domain.point.model.dto.response.PointResponse;
import io.why503.paymentservice.domain.point.model.entity.Point;
import org.springframework.stereotype.Component;

/**
 * 포인트 엔티티 데이터를 응답용 객체로 변환하는 컴포넌트
 */
@Component
public class PointMapper {

    // 포인트 엔티티 정보를 상태 설명이 포함된 응답 DTO로 변환
    public PointResponse entityToResponse(Point point) {
        if (point == null) {
            throw new IllegalArgumentException("변환할 Point Entity는 필수입니다.");
        }

        return new PointResponse(
                point.getSq(),
                point.getOrderId(),
                point.getChargeAmount(),
                point.getStatus().name(),
                point.getStatus().getDescription(),
                point.getCreatedDt()
        );
    }

    public Point responseToEntity(Long userSq, String orderId, Long chargeAmount) {
        return Point.builder()
                .userSq(userSq)
                .orderId(orderId)
                .chargeAmount(chargeAmount)
                .build();
    }
}