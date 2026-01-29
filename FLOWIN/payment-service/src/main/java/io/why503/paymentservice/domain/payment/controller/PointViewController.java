package io.why503.paymentservice.domain.payment.controller;

import io.why503.paymentservice.domain.payment.config.TossPaymentConfig;
import io.why503.paymentservice.global.client.AccountClient;
import io.why503.paymentservice.global.client.dto.response.AccountResponse;
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
    private final TossPaymentConfig tossPaymentConfig;

    // 포인트 충전 페이지
    // 접속 주소: http://localhost:8000/payment-view/point/charge
    @GetMapping("/charge")
    public String chargePage(
            @RequestHeader(value = "X-USER-SQ", required = false) Long userSq,
            Model model
    ) {
        // 1. [보안] 로그인 여부 검증
        if (userSq == null) {
            throw new IllegalArgumentException("로그인이 필요한 서비스입니다.");
        }

        // 2. [조회] 회원 정보 (이름, 포인트) 가져오기
        try {
            AccountResponse account = accountClient.getAccount(userSq);
            model.addAttribute("userName", account.userName());
            // 포인트가 null일 경우 0으로 처리 (NullPointerException 방지)
            model.addAttribute("currentPoint", account.userPoint() != null ? account.userPoint() : 0L);
        } catch (Exception e) {
            // 타 서비스 장애 시에도 충전 페이지는 진입 가능하도록 처리
            log.error(">>> [PointView] 회원 정보 조회 실패 (UserSq={}): {}", userSq, e.getMessage());
            model.addAttribute("userName", "회원");
            model.addAttribute("currentPoint", 0L);
        }

        // 3. 결제 위젯 설정
        model.addAttribute("clientKey", tossPaymentConfig.getClientKey());
        model.addAttribute("userSq", userSq);

        return "charge";
    }
}