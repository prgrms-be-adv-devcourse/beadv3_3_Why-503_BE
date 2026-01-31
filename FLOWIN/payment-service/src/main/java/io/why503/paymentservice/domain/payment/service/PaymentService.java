package io.why503.paymentservice.domain.payment.service;

import io.why503.paymentservice.domain.payment.model.dto.request.PaymentRequest;
import io.why503.paymentservice.domain.payment.model.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    /**
     * 결제 승인 및 처리 (통합)
     * - 예매(Booking) 또는 포인트충전(Point) 건에 대한 결제를 수행합니다.
     * - 복합 결제(MIX)인 경우, PG사 승인과 포인트 차감을 동시에 처리합니다.
     * - PG 결제가 포함된 경우, 외부 PG API를 호출하여 최종 승인을 받습니다.
     */
    PaymentResponse pay(Long userSq, PaymentRequest request);

    /**
     * 결제 상세 조회
     * - 본인의 결제 내역만 조회 가능합니다.
     */
    PaymentResponse findPayment(Long userSq, Long paymentSq);

    /**
     * 내 결제 이력 조회
     * - 최신순으로 정렬된 전체 이력을 반환합니다.
     */
    List<PaymentResponse> findPaymentsByUser(Long userSq);

    /**
     * 결제 취소
     * - 결제 상태를 취소로 변경합니다.
     * - PG 결제 건은 PG사에 취소 요청을 보냅니다.
     * - 포인트 사용 건은 포인트를 환불(재적립)합니다.
     */
    PaymentResponse cancelPayment(Long userSq, Long paymentSq, String reason);
}