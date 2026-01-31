package io.why503.paymentservice.global.client;

public interface PgClient {

    /**
     * PG사 결제 승인 요청
     * @param paymentKey 클라이언트(프론트)에서 받은 결제 키
     * @param orderId 주문 번호
     * @param amount 결제 금액
     * @return pgKey (PG사에서 발급한 최종 거래 고유 번호)
     */
    String approvePayment(String paymentKey, String orderId, Long amount);

    /**
     * PG사 결제 취소 요청
     * @param pgKey PG사 거래 고유 번호 (approvePayment의 리턴값)
     * @param reason 취소 사유
     */
    void cancelPayment(String pgKey, String reason, Long cancelAmount);
}