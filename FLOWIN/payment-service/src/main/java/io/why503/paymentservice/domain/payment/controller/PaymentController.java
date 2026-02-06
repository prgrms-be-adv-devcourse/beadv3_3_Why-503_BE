package io.why503.paymentservice.domain.payment.controller;

import io.why503.paymentservice.domain.payment.model.dto.request.PaymentCancelRequest;
import io.why503.paymentservice.domain.payment.model.dto.request.PaymentRequest;
import io.why503.paymentservice.domain.payment.model.dto.response.PaymentResponse;
import io.why503.paymentservice.domain.payment.service.PaymentService;
import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 결제 승인, 조회, 취소 요청을 처리하는 컨트롤러
 * - Gateway에서 전달된 X-USER-SQ 헤더를 기반으로 사용자 인증 처리
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 승인 요청
     * [POST] /payments
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> pay(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody @Valid PaymentRequest request) {

        validateUserHeader(userSq);
        PaymentResponse response = paymentService.pay(userSq, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 내 결제 목록 조회
     * [GET] /payments
     */
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findPayments(
            @RequestHeader("X-USER-SQ") Long userSq) {

        validateUserHeader(userSq);
        return ResponseEntity.ok(paymentService.findPaymentsByUser(userSq));
    }

    /**
     * 결제 상세 조회
     * [GET] /payments/{paymentSq}
     */
    @GetMapping("/{paymentSq}")
    public ResponseEntity<PaymentResponse> findPayment(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long paymentSq) {

        validateUserHeader(userSq);
        return ResponseEntity.ok(paymentService.findPayment(userSq, paymentSq));
    }

    /**
     * 결제 취소 요청
     * [POST] /payments/{paymentSq}/cancel
     */
    @PostMapping("/{paymentSq}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long paymentSq,
            @RequestBody(required = false) PaymentCancelRequest request) {

        validateUserHeader(userSq);

        String reason = (request != null && request.reason() != null) ? request.reason() : "사용자 요청에 의한 취소";

        return ResponseEntity.ok(paymentService.cancelPayment(userSq, paymentSq, reason));
    }

    private void validateUserHeader(Long userSq) {
        if (userSq == null || userSq <= 0) {
            throw PaymentExceptionFactory.paymentForbidden("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }
    }
}