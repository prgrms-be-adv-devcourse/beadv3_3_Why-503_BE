package io.why503.reservationservice.domain.booking.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 예매 상태 관리 Enum
 * - PENDING: 결제 대기 (좌석 선점 중)
 * - PAID: 결제 완료 (예매 확정)
 * - CANCELLED: 예매 취소 (사용자 취소 또는 미결제 만료)
 */
@Getter
@AllArgsConstructor
public enum BookingStatus {

    PENDING("결제 대기"),
    PAID("결제 완료"),
    CANCELLED("예매 취소");

    private final String description;
}