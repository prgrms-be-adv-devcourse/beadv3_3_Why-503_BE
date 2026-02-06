package io.why503.reservationservice.domain.booking.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 예매 프로세스의 진행 상태를 관리하는 열거형
 */
@Getter
@AllArgsConstructor
public enum BookingStatus {
    PENDING("예매대기"),
    CONFIRMED("예매완료"),
    CANCELLED("예매취소"),
    PARTIAL_CANCEL("부분취소");

    private final String description;
}