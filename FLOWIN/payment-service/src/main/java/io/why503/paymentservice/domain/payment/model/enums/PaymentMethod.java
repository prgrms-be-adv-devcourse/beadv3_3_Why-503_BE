package io.why503.paymentservice.domain.payment.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 결제 거래에 사용된 수단에 대한 구분
 */
@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CARD("카드"),
    POINT("포인트"),
    MIX("복합결제");

    private final String description;
}