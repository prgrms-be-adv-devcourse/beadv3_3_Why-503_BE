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

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 통합 결제 승인 요청
     * - 예매(Booking) 또는 포인트충전(Point) 건에 대한 결제를 수행합니다.
     * - 복합 결제(카드+포인트) 로직도 이곳에서 시작됩니다.
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> pay(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody @Valid PaymentRequest request) {

        // 해피 패스 금지: 헤더 값 검증
        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }

        PaymentResponse response = paymentService.pay(userSq, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 결제 상세 조회
     * - 본인의 결제 내역만 조회 가능
     */
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

    /**
     * 내 결제 이력 조회
     * - 전체 결제 내역을 최신순으로 조회
     */
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findPayments(
            @RequestHeader("X-USER-SQ") Long userSq) {

        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }

        List<PaymentResponse> responses = paymentService.findPaymentsByUser(userSq);
        return ResponseEntity.ok(responses);
    }

    /**
     * 결제 취소
     * - PG 결제 취소 및 포인트 환불을 수행합니다.
     * - 취소 사유(reason)를 Body로 받습니다. (Map 사용으로 DTO 생성 최소화)
     */
    @PostMapping("/{paymentSq}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable("paymentSq") Long paymentSq,
            @RequestBody Map<String, String> requestBody) {

        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }

        String reason = requestBody.get("reason");
        // 해피 패스 금지: 사유 필수 체크
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("취소 사유(reason)는 필수입니다.");
        }

        PaymentResponse response = paymentService.cancelPayment(userSq, paymentSq, reason);
        return ResponseEntity.ok(response);
    }
}