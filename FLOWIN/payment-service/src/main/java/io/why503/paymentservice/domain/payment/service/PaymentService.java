package io.why503.paymentservice.domain.payment.service;

import io.why503.paymentservice.domain.payment.dto.request.PaymentCancelRequest;
import io.why503.paymentservice.domain.payment.dto.request.PaymentConfirmRequest;
import io.why503.paymentservice.domain.payment.dto.request.PointChargeRequest;
import io.why503.paymentservice.domain.payment.dto.response.PointChargeResponse;

/**
 * 결제 서비스 인터페이스
 * - PG사(토스) 연동 및 결제 상태 관리
 */
public interface PaymentService {

    // 결제 승인
    void confirmPayment(PaymentConfirmRequest request, Long userSq);

    // 결제 취소
    void cancelPayment(PaymentCancelRequest request, Long userSq);

    // 결제 실패 처리
    void failPayment(String orderId, String message, Long userSq);

    // 포인트 충전 요청
    PointChargeResponse requestPointCharge(Long userSq, PointChargeRequest request);
}