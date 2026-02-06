package io.why503.paymentservice.domain.payment.model.dto.response;

import java.time.LocalDateTime;

/**
 * 결제 결과 상세 응답 DTO (12개 필드)
 */
public record PaymentResponse(
        Long sq,
        String orderId,
        String refType,        // "BOOKING" 또는 "POINT"
        String method,         // "CARD", "MIX" 등 코드값
        String methodDescription, // "카드", "복합결제" 등 한글명
        String status,         // "READY", "DONE" 등 코드값
        String statusDescription, // "결제 준비", "결제 완료" 등 한글명
        Long totalAmount,
        Long pgAmount,
        Long pointAmount,
        LocalDateTime approvedDt,
        LocalDateTime createdDt
) {
}