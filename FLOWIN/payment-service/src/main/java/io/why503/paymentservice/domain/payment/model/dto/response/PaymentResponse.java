package io.why503.paymentservice.domain.payment.model.dto.response;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long sq,
        String orderId,

        // 결제 대상 (BOOKING, POINT)
        String refType,

        // 결제 수단 (CARD, POINT, MIX)
        String method,
        String methodDescription, // 예: 복합결제

        // 결제 상태 (READY, DONE, CANCELED)
        String status,
        String statusDescription, // 예: 결제완료

        // 금액 상세
        Long totalAmount,
        Long pgAmount,
        Long pointAmount,

        // 일시 정보
        LocalDateTime approvedDt,
        LocalDateTime cancelledDt,
        LocalDateTime createdDt
) {
}