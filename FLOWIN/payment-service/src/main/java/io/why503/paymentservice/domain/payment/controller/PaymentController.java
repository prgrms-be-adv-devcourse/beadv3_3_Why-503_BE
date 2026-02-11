package io.why503.paymentservice.domain.payment.controller;

import io.why503.paymentservice.domain.payment.model.dto.request.PaymentCancelRequest;
import io.why503.paymentservice.domain.payment.model.dto.request.PaymentRequest;
import io.why503.paymentservice.domain.payment.model.dto.response.PaymentResponse;
import io.why503.paymentservice.domain.payment.service.PaymentService;
import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory;
import io.why503.paymentservice.domain.point.model.dto.response.PointResponse;
import io.why503.paymentservice.global.client.AccountClient;
import io.why503.paymentservice.global.client.dto.response.AccountResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 결제 승인, 조회, 취소 프로세스를 제어하는 컨트롤러
 * - 외부 결제 시스템과의 연동 결과 반영 및 결제 이력 관리
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final AccountClient accountClient;
    private final PaymentService paymentService;

    // 결제 수단 검증 및 최종 결제 승인 처리
    @PostMapping
    public ResponseEntity<PaymentResponse> pay(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody @Valid PaymentRequest request) {

        validateUserHeader(userSq);
        PaymentResponse response = paymentService.pay(userSq, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 본인의 과거 결제 성공 및 취소 이력 전체 조회
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findPayments(
            @RequestHeader("X-USER-SQ") Long userSq) {

        validateUserHeader(userSq);
        return ResponseEntity.ok(paymentService.findPaymentsByUser(userSq));
    }

    @GetMapping("/{paymentSq}")
    public ResponseEntity<PaymentResponse> findPayment(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long paymentSq) {

        validateUserHeader(userSq);
        return ResponseEntity.ok(paymentService.findPayment(userSq, paymentSq));
    }

    // 승인된 결제건에 대한 환불 및 거래 무효화
    @PostMapping("/{paymentSq}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long paymentSq,
            @RequestBody @Valid PaymentCancelRequest request) {

        validateUserHeader(userSq);
        return ResponseEntity.ok(paymentService.cancelPayment(userSq, paymentSq, request));
    }

    @GetMapping("/points")
    public ResponseEntity<AccountResponse> findMyPoints(
            @RequestHeader("X-USER-SQ") Long userSq) {

        validateUserHeader(userSq);
        AccountResponse response = accountClient.getAccount(userSq);
        return ResponseEntity.ok(response);
    }

    // 게이트웨이를 통해 전달된 필수 사용자 식별값 검증
    private void validateUserHeader(Long userSq) {
        if (userSq == null || userSq <= 0) {
            throw PaymentExceptionFactory.paymentForbidden("유효하지 않은 사용자입니다.");
        }
    }
}