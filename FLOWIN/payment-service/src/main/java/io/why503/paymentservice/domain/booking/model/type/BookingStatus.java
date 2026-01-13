package io.why503.paymentservice.domain.booking.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookingStatus {
    // 순서가 중요합니다! (DB 저장값: 0, 1, 2, 3)
    PENDING("예매대기"),       // 0
    CONFIRMED("예매완료"),      // 1
    CANCELLED("예매취소"),      // 2 (전체 취소)
    PARTIAL_CANCEL("부분취소"); // 3 (일부 취소) - ★ 추가됨

    private final String description;
}