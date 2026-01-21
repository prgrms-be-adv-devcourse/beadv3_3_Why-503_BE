package io.why503.paymentservice.domain.payment.controller;

import io.why503.paymentservice.domain.payment.dto.PaymentCancelRequest;
import io.why503.paymentservice.domain.payment.dto.PaymentConfirmRequest;
import io.why503.paymentservice.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 결제 API 컨트롤러
 * - 내부 서비스 간 통신(Feign)이나 프론트엔드의 비동기 요청(Ajax)을 처리합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/payments")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 승인
     * - PG사 결제창에서 승인된 후 호출됩니다.
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmRequest request) {
        paymentService.confirmPayment(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 결제 취소 (전체/부분)
     * - 예매 전체 취소 또는 특정 티켓 부분 취소를 처리합니다.
     */
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelPayment(@RequestBody PaymentCancelRequest request) {
        paymentService.cancelPayment(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 결제 실패 처리
     * - PG사 연동 과정에서 실패했을 때 상태를 업데이트합니다.
     */
    @PostMapping("/fail")
    public void failPayment(
            @RequestParam String orderId,
            @RequestParam(required = false) String message
    ) {
        paymentService.failPayment(orderId, message);
    }
}