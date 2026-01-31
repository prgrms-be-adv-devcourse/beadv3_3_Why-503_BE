package io.why503.paymentservice.domain.point.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 포인트 충전 상태 VO
 * - 포인트 충전 요청의 생명주기를 관리합니다.
 */
@Getter
@AllArgsConstructor
public enum PointStatus {
    READY("충전대기"),    // 결제 진입 전
    DONE("충전완료"),     // 결제 성공 및 포인트 적립 완료
    CANCELED("충전취소"); // 사용자 취소 또는 결제 실패

    private final String description;

    // String -> Enum 변환 메서드
    public static PointStatus from(String status) {
        // 해피 패스 금지: null 또는 빈 값 검증
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("PointStatus는 필수 값입니다.");
        }

        // 메서드 참조(::) 금지: 람다식 사용
        return Arrays.stream(PointStatus.values())
                .filter(s -> s.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 PointStatus 입니다: " + status));
    }
}