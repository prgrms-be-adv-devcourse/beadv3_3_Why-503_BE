package io.why503.reservationservice.domain.booking.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 예매 및 좌석 선점의 생명주기를 관리하는 상태값
 */
@Getter
@AllArgsConstructor
public enum BookingStatus {

    PENDING("결제 대기"),
    PAID("결제 완료"),
    CANCELLED("예매 취소");

    private final String description;
}