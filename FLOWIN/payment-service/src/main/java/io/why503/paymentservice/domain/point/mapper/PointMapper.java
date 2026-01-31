package io.why503.paymentservice.domain.point.mapper;

import io.why503.paymentservice.domain.point.model.dto.response.PointResponse;
import io.why503.paymentservice.domain.point.model.entity.Point;
import org.springframework.stereotype.Component;

@Component
public class PointMapper {

    /**
     * Point Entity -> PointResponse DTO 변환
     * - 상태값(Status)을 코드와 설명으로 분리하여 매핑합니다.
     */
    public PointResponse entityToResponse(Point point) {
        // 해피 패스 금지: Entity 필수 검증
        if (point == null) {
            throw new IllegalArgumentException("변환할 Point Entity는 필수입니다.");
        }

        return new PointResponse(
                point.getSq(),
                point.getOrderId(),
                point.getChargeAmount(),
                point.getStatus().name(),           // 상태 코드 (예: DONE)
                point.getStatus().getDescription(), // 상태 설명 (예: 충전완료)
                point.getCreatedDt()
        );
    }
}