package io.why503.paymentservice.domain.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 토스 페이먼츠 응답 DTO
 * - 결제 승인 API 호출 결과로 반환되는 PG사 데이터입니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class TossPaymentResponse {

    private String paymentKey;
    private String orderId;
    private String status;      // 결제 상태 (READY, DONE, CANCELED 등)
    private Long totalAmount;   // 총 결제 금액
    private String method;      // 결제 수단 (카드, 가상계좌 등)
    private String approvedAt;  // 승인 일시 (ISO 8601)
    private Receipt receipt;    // 영수증 정보

    @Getter
    @NoArgsConstructor
    public static class Receipt {
        private String url; // 영수증 확인 URL
    }
}