package io.why503.paymentservice.domain.payment.controller;

import io.why503.paymentservice.domain.payment.dto.PaymentConfirmRequest;
import io.why503.paymentservice.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(
            @RequestBody PaymentConfirmRequest request
    ) {
        paymentService.confirmPayment(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fail")
    public void failPayment(
            @RequestParam String orderId,
            @RequestParam(required = false) String message
    ) {
        paymentService.failPayment(orderId, message);
    }
}
