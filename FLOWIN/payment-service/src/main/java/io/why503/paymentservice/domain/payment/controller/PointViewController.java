package io.why503.paymentservice.domain.payment.controller;

import io.why503.paymentservice.global.client.AccountClient;
import io.why503.paymentservice.global.client.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 포인트 충전 화면 컨트롤러 (View)
 * - 포인트 충전 관련 페이지만 담당합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/payment-view/point") // URL 경로를 조금 구체화했습니다.
public class PointViewController {

    private final AccountClient accountClient;

    @Value("${payment.client-key}")
    private String clientKey;

    /**
     * 포인트 충전 페이지
     * - 접속 주소: http://localhost:8000/payment-view/point/charge
     */
    @GetMapping("/charge")
    public String chargePage(
            @RequestHeader(value = "X-USER-SQ", required = false) Long userSq,
            Model model
    ) {
        // 1. [보안] 로그인 여부 검증 (Gateway 헤더 필수로 변경)
        if (userSq == null) {
            throw new IllegalArgumentException("로그인이 필요한 서비스입니다.");
        }

        // 2. [조회] 회원 정보(이름, 포인트) 가져오기
        // (AccountClient가 getAccount(sq) 메서드를 가지고 있다고 가정)
        try {
            AccountResponse account = accountClient.getAccount(userSq);
            model.addAttribute("userName", account.userName()); // 혹은 account.getName()
            model.addAttribute("currentPoint", account.userPoint()); // 현재 포인트
        } catch (Exception e) {
            log.error("회원 정보 조회 실패: {}", e.getMessage());
            // 조회 실패해도 충전은 가능하게 하려면 예외를 삼키거나,
            // 에러 페이지로 보내려면 throw e;
            model.addAttribute("userName", "회원");
            model.addAttribute("currentPoint", 0);
        }

        model.addAttribute("clientKey", clientKey);
        model.addAttribute("userSq", userSq);

        return "charge";
    }
}