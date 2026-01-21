package io.why503.paymentservice.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 결제 취소 요청 DTO
 * - 전체 취소 또는 부분 취소(티켓 단위) 요청 시 사용됩니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelRequest {

    private String orderId;       // 주문 식별자 (필수)
    private Long ticketSq;        // 취소할 티켓 ID (Null이면 전체 취소, 값이 있으면 부분 취소)
    private String cancelReason;  // 취소 사유
    private Integer cancelAmount; // 취소 금액 (검증용)
}