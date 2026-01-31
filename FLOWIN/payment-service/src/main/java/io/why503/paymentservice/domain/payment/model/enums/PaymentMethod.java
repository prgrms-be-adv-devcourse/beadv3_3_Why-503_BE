package io.why503.paymentservice.domain.payment.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CARD("카드"),          // PG 전액 결제
    POINT("포인트"),        // 포인트 전액 결제
    MIX("복합결제");        // 카드 + 포인트

    private final String description;
}