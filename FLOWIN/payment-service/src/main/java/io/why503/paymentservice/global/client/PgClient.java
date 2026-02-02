package io.why503.paymentservice.global.client;

/**
 * 외부 결제 대행사(PG)와의 결제 승인 및 취소 통신을 정의하는 인터페이스
 */
public interface PgClient {

    // 결제창 인증 정보를 기반으로 외부 PG사에 최종 승인 요청
    String approvePayment(String paymentKey, String orderId, Long amount);

    // 승인된 거래에 대해 외부 PG사에 취소 및 환불 요청
    void cancelPayment(String pgKey, String reason, Long cancelAmount);
}