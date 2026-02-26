package io.why503.paymentservice.domain.payment.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 결제 거래 처리 상태에 대한 구분
 */
@Getter
@AllArgsConstructor
public enum PaymentStatus {
    READY("결제 준비"),
    DONE("결제 완료"),
    PARTIAL_CANCELED("부분 취소"),
    CANCELED("결제 취소");

    private final String description;
}