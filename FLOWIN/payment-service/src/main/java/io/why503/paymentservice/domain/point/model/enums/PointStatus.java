package io.why503.paymentservice.domain.point.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 포인트 충전 요청의 생명주기를 관리하는 열거형
 */
@Getter
@AllArgsConstructor
public enum PointStatus {
    READY("충전대기"),
    DONE("충전완료"),
    CANCELED("충전취소");

    private final String description;

    // 문자열 상태값을 매칭되는 Enum 상수로 변환
    public static PointStatus from(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("PointStatus는 필수 값입니다.");
        }

        return Arrays.stream(PointStatus.values())
                .filter(s -> s.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 PointStatus 입니다: " + status));
    }
}