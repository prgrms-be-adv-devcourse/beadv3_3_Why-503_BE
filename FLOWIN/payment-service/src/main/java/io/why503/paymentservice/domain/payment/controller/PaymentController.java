package io.why503.paymentservice.domain.payment.controller;

import io.why503.paymentservice.domain.payment.model.dto.request.PaymentCancelRequest;
import io.why503.paymentservice.domain.payment.model.dto.request.PaymentRequest;
import io.why503.paymentservice.domain.payment.model.dto.response.PaymentResponse;
import io.why503.paymentservice.domain.payment.service.PaymentService;
import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory;
import io.why503.paymentservice.global.client.AccountClient;
import io.why503.paymentservice.global.client.dto.response.AccountResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 결제 거래의 시작부터 완료 및 사후 취소까지의 전 과정을 제어하는 접점
 * - 외부 시스템과의 연동 결과를 통합하여 거래의 최종 상태를 확정
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final AccountClient accountClient;
    private final PaymentService paymentService;

    // 결제 수단의 유효성을 검증하고 요청된 금액에 대한 최종 승인 절차 수행
    @PostMapping
    public ResponseEntity<PaymentResponse> pay(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody @Valid PaymentRequest request) {

        validateUserHeader(userSq);
        PaymentResponse response = paymentService.pay(userSq, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 사용자가 수행한 전체 결제 내역과 각 거래별 상세 진행 상태 확인
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findPayments(
            @RequestHeader("X-USER-SQ") Long userSq) {

        validateUserHeader(userSq);
        return ResponseEntity.ok(paymentService.findPaymentsByUser(userSq));
    }

    // 특정 결제 건에 대한 상세 승인 정보 및 정산 데이터 추출
    @GetMapping("/{paymentSq}")
    public ResponseEntity<PaymentResponse> findPayment(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long paymentSq) {

        validateUserHeader(userSq);
        return ResponseEntity.ok(paymentService.findPayment(userSq, paymentSq));
    }

    // 이미 완료된 거래에 대해 부분 또는 전액 환불을 요청하고 소유권 회수
    @PostMapping("/{paymentSq}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long paymentSq,
            @RequestBody @Valid PaymentCancelRequest request) {

        validateUserHeader(userSq);
        return ResponseEntity.ok(paymentService.cancelPayment(userSq, paymentSq, request));
    }

    // 결제에 사용 가능한 사용자의 잔여 포인트 현황 조회
    @GetMapping("/points")
    public ResponseEntity<AccountResponse> findMyPoints(
            @RequestHeader("X-USER-SQ") Long userSq) {

        validateUserHeader(userSq);
        AccountResponse response = accountClient.getAccount(userSq);
        return ResponseEntity.ok(response);
    }

    // 보안 및 거래 정합성을 위한 요청자 식별 정보의 유효성 검사
    private void validateUserHeader(Long userSq) {
        if (userSq == null || userSq <= 0) {
            throw PaymentExceptionFactory.paymentForbidden("유효하지 않은 사용자입니다.");
        }
    }
}