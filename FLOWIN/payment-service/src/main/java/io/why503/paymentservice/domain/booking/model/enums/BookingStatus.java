package io.why503.paymentservice.domain.booking.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 예매 상태 VO
 * - 예매의 전체적인 진행 상황을 관리합니다.
 */
@Getter
@AllArgsConstructor
public enum BookingStatus {
    PENDING("예매대기"),       // 결제 전
    CONFIRMED("예매완료"),      // 결제 완료
    CANCELLED("예매취소"),      // 전체 취소
    PARTIAL_CANCEL("부분취소"); // 일부 티켓만 취소됨

    private final String description;

    // String -> Enum 변환 메서드
    public static BookingStatus from(String status) {
        // 해피 패스 금지: null 또는 빈 값 검증
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("BookingStatus는 필수 값입니다.");
        }

        // 메서드 참조(::) 금지: 람다식 사용
        return Arrays.stream(BookingStatus.values())
                .filter(s -> s.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 BookingStatus 입니다: " + status));
    }
}