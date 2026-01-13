package io.why503.paymentservice.domain.booking.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TicketStatus {
    // DB에 0, 1, 2, 3, 4 순서대로 저장됩니다.
    AVAILABLE("예매가능"), // 0
    RESERVED("선점됨"),   // 1
    PAID("결제됨"),       // 2
    USED("사용됨"),       // 3
    CANCELLED("취소됨");  // 4

    private final String description;
}