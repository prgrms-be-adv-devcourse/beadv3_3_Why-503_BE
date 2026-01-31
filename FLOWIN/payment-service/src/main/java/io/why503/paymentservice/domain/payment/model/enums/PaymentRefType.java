package io.why503.paymentservice.domain.payment.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentRefType {
    BOOKING("예매"),   // 예매 결제
    POINT("포인트충전"); // 포인트 충전 결제

    private final String description;
}