package io.why503.paymentservice.domain.payment.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    READY("결제대기"),
    DONE("결제완료"),
    CANCELED("결제취소");

    private final String description;
}