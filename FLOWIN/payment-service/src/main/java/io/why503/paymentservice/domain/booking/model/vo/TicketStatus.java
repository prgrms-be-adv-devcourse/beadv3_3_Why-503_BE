package io.why503.paymentservice.domain.booking.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TicketStatus {
    AVAILABLE("예매가능"), // 0
    RESERVED("선점됨"),   // 1
    PAID("결제됨"),       // 2
    USED("사용됨"),       // 3
    CANCELLED("취소됨");  // 4

    private final String description;
}