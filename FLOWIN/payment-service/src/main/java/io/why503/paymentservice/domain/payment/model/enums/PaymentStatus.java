package io.why503.paymentservice.domain.payment.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    READY("결제 준비"),     //
    DONE("결제 완료"),      //
    PARTIAL_CANCELED("부분 취소"), // SQL 스키마 확장을 위해 추가
    CANCELED("결제 취소");   //

    private final String description;
}