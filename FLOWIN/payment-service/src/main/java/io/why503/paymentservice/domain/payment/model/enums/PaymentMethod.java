package io.why503.paymentservice.domain.payment.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 결제 시 사용된 주요 수단(카드, 포인트, 복합)을 정의하는 열거형
 */
@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CARD("카드"),
    POINT("포인트"),
    MIX("복합결제");

    private final String description;
}