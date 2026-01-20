package io.why503.paymentservice.domain.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossPaymentResponse {
    private String paymentKey;
    private String orderId;
    private String status;         // READY, IN_PROGRESS, DONE, CANCELED 등
    private Long totalAmount;
    private String method;         // 카드, 가상계좌 등
    private String approvedAt;     // 결제 승인 시각
    private Receipt receipt;       // 영수증 정보

    @Getter
    @NoArgsConstructor
    public static class Receipt {
        private String url;        // 영수증 확인 URL
    }
}