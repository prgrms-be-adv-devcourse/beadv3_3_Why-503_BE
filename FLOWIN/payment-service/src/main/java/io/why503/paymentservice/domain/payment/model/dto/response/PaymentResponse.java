package io.why503.paymentservice.domain.payment.model.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

import java.time.LocalDateTime;

/**
 * 결제 결과와 승인/취소 일시 등 결제 상세 정보를 전달하는 응답 객체
 */
public record PaymentResponse(
        @NonNull
        Long sq,
        @NotBlank
        String orderId,
        @NotBlank
        String refType,
        @NotBlank
        String method,
        @NotBlank
        String methodDescription,
        @NotBlank
        String status,
        @NotBlank
        String statusDescription,
        @NonNull
        Long totalAmount,
        @NonNull
        Long pgAmount,
        @NonNull
        Long pointAmount,
        LocalDateTime approvedDt,
        LocalDateTime cancelledDt,
        @NonNull
        LocalDateTime createdDt
) {
}