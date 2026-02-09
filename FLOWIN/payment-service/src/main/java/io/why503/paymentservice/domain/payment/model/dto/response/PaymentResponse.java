package io.why503.paymentservice.domain.payment.model.dto.response;

import java.time.LocalDateTime;

/**
 * 결제 처리 결과 및 상세 이력 정보를 반환하는 객체
 * - 결제 수단, 상태, 금액 구성 및 승인 시점 정보를 포함
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
        LocalDateTime createdDt
) {
}