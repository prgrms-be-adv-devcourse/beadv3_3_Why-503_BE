package io.why503.paymentservice.domain.payment.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 결제가 발생한 원천 대상(예매, 포인트 충전)을 구분하는 열거형
 */
@Getter
@AllArgsConstructor
public enum PaymentRefType {
    BOOKING("예매"),
    POINT("포인트충전");

    private final String description;
}