package io.why503.paymentservice.domain.payment.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 결제 거래가 발생한 상품에 대한 구분
 */
@Getter
@AllArgsConstructor
public enum PaymentRefType {
    BOOKING("공연 예매 결제"),
    POINT("포인트 충전 결제");

    private final String description;
}