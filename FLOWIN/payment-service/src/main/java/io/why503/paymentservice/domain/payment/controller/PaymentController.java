package io.why503.paymentservice.domain.payment.controller;

import io.why503.paymentservice.domain.payment.dto.request.PaymentCancelRequest;
import io.why503.paymentservice.domain.payment.dto.request.PaymentConfirmRequest;
import io.why503.paymentservice.domain.payment.dto.request.PointChargeRequest;
import io.why503.paymentservice.domain.payment.dto.response.PointChargeResponse;
import io.why503.paymentservice.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 결제 API 컨트롤러
 * - 내부 서비스 간 통신(Feign)이나 프론트엔드의 비동기 요청(Ajax)을 처리합니다.
 * - [수정] 보안을 위해 모든 요청에 대해 사용자 검증(X-USER-SQ)을 수행합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment-api")
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 승인
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody PaymentConfirmRequest request
    ) {
        paymentService.confirmPayment(request, userSq);
        return ResponseEntity.ok().build();
    }

    // 결제 취소 (전체/부분)
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelPayment(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody PaymentCancelRequest request
    ) {
        paymentService.cancelPayment(request, userSq);
        return ResponseEntity.ok().build();
    }

    // 결제 실패 처리
    @PostMapping("/fail")
    public void failPayment(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestParam String orderId,
            @RequestParam(required = false) String message
    ) {
        paymentService.failPayment(orderId, message, userSq);
    }

    // 포인트 충전 요청
    @PostMapping("/point/request")
    public ResponseEntity<PointChargeResponse> requestPointCharge(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody PointChargeRequest request
    ) {
        // PointChargeRequest는 { Long amount } 필드만 가짐
        PointChargeResponse response = paymentService.requestPointCharge(userSq, request);
        return ResponseEntity.ok(response);
    }
}