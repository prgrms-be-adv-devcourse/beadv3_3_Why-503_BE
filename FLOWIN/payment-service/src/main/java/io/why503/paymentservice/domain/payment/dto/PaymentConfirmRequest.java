package io.why503.paymentservice.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 결제 승인 요청 DTO
 * - PG사 결제창 승인 성공 후, 최종 승인을 요청할 때 사용됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequest {

    private String paymentKey; // PG사 승인 키
    private String orderId;    // 주문 ID
    private Integer amount;    // 결제 요청 금액
}