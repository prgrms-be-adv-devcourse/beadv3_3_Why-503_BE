package io.why503.paymentservice.domain.payment.controller;

import io.why503.paymentservice.domain.payment.model.dto.request.PaymentRequest;
import io.why503.paymentservice.domain.payment.model.dto.response.PaymentResponse;
import io.why503.paymentservice.domain.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 결제 승인 요청 처리 및 결제 이력 조회를 담당하는 컨트롤러
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 예매 또는 포인트 충전에 대한 통합 결제 승인 요청 처리
    @PostMapping
    public ResponseEntity<PaymentResponse> pay(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody @Valid PaymentRequest request) {

        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }

        PaymentResponse response = paymentService.pay(userSq, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 특정 결제 건에 대한 상세 내역 조회
    @GetMapping("/{paymentSq}")
    public ResponseEntity<PaymentResponse> findPayment(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable("paymentSq") Long paymentSq) {

        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }

        PaymentResponse response = paymentService.findPayment(userSq, paymentSq);
        return ResponseEntity.ok(response);
    }

    // 사용자의 전체 결제 이력 목록 조회
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findPayments(
            @RequestHeader("X-USER-SQ") Long userSq) {

        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }

        List<PaymentResponse> responses = paymentService.findPaymentsByUser(userSq);
        return ResponseEntity.ok(responses);
    }

    // 완료된 결제의 취소 및 환불 처리
    @PostMapping("/{paymentSq}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable("paymentSq") Long paymentSq,
            @RequestBody Map<String, String> requestBody) {

        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }

        String reason = requestBody.get("reason");
        PaymentResponse response = paymentService.cancelPayment(userSq, paymentSq, reason);
        return ResponseEntity.ok(response);
    }
}