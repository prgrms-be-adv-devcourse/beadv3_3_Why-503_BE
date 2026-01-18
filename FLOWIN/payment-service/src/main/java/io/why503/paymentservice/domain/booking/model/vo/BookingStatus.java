package io.why503.paymentservice.domain.booking.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookingStatus {
    PENDING("예매대기"),       // 0
    CONFIRMED("예매완료"),      // 1
    CANCELLED("예매취소"),      // 2 (전체 취소)
    PARTIAL_CANCEL("부분취소"); // 3 (일부 취소)

    private final String description;
}