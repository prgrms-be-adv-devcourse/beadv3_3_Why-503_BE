package io.why503.paymentservice.domain.payment.model.dto.response;

import java.time.LocalDateTime;

/**
 * 결제 결과와 승인/취소 일시 등 결제 상세 정보를 전달하는 응답 객체
 */
public record PaymentResponse(
        Long sq,
        String orderId,
        String refType,
        String method,
        String methodDescription,
        String status,
        String statusDescription,
        Long totalAmount,
        Long pgAmount,
        Long pointAmount,
        LocalDateTime approvedDt,
        LocalDateTime cancelledDt,
        LocalDateTime createdDt
) {
}