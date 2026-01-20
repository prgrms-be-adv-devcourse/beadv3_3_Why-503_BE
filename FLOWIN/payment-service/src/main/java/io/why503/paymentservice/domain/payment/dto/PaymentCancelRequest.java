package io.why503.paymentservice.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelRequest {
    private String orderId;       // [추가] 주문 식별자 (필수)
    private Long ticketSq;        // [추가] 개별 취소할 티켓 시퀀스 (부분 취소 시 사용, 전체 취소면 null)
    private String cancelReason;  // 취소 사유
    private Integer cancelAmount; // 취소할 금액 (검증용 또는 수동 입력용)
}