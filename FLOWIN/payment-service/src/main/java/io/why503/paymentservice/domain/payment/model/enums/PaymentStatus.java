package io.why503.paymentservice.domain.payment.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 결제 승인 프로세스의 처리 상태를 정의하는 열거형
 */
@Getter
@AllArgsConstructor
public enum PaymentStatus {
    READY("결제대기"),
    DONE("결제완료"),
    CANCELED("결제취소");

    private final String description;
}