package io.why503.paymentservice.domain.payment.controller;

import io.why503.paymentservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.paymentservice.domain.booking.service.BookingService;
import io.why503.paymentservice.domain.payment.config.TossPaymentConfig;
import io.why503.paymentservice.domain.point.model.dto.response.PointResponse;
import io.why503.paymentservice.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 결제 진입 페이지 및 성공/실패 결과 화면을 제어하는 컨트롤러
 */
@Controller
@RequestMapping("/payment-view")
@RequiredArgsConstructor
public class PaymentViewController {

    private final BookingService bookingService;
    private final PointService pointService;
    private final TossPaymentConfig tossPaymentConfig;

    // 예매 결제를 위한 체크아웃 화면 렌더링
    @GetMapping("/checkout/booking/{bookingSq}")
    public String renderBookingCheckout(@PathVariable Long bookingSq, Model model) {
        try {
            BookingResponse booking = bookingService.findBooking(1L, bookingSq);
            model.addAttribute("booking", booking);
            model.addAttribute("clientKey", tossPaymentConfig.getClientKey());
            return "checkout";
        } catch (Exception e) {
            return this.renderFailWithMsg(e.getMessage(), "CHECKOUT_ERROR", model);
        }
    }

    // 포인트 충전 결제를 위한 체크아웃 화면 렌더링
    @GetMapping("/checkout/point/{pointSq}")
    public String renderPointCheckout(@PathVariable Long pointSq, Model model) {
        try {
            PointResponse point = pointService.findPoint(1L, pointSq);
            model.addAttribute("point", point);
            model.addAttribute("clientKey", tossPaymentConfig.getClientKey());
            return "checkout";
        } catch (Exception e) {
            return this.renderFailWithMsg(e.getMessage(), "CHECKOUT_ERROR", model);
        }
    }

    // 외부 결제 승인 성공 시 안내 페이지 표시
    @GetMapping("/success")
    public String renderSuccessPage(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            Model model) {

        if (paymentKey == null || orderId == null || amount == null) {
            return this.renderFailWithMsg("결제 승인 정보가 부족합니다.", "INVALID_PARAMS", model);
        }

        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);

        String productName = "결제 상품";
        if (orderId.startsWith("BOOKING")) {
            productName = "공연 예매";
        } else if (orderId.startsWith("POINT")) {
            productName = "포인트 충전";
        }
        model.addAttribute("productName", productName);

        return "success";
    }

    // 결제 실패 시 사유와 함께 에러 페이지 표시
    @GetMapping("/fail")
    public String renderFailPage(
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String orderId,
            Model model) {

        model.addAttribute("message", message != null ? message : "결제 실패");
        model.addAttribute("code", code != null ? code : "ERROR");
        model.addAttribute("orderId", orderId);

        return "fail";
    }

    // 예외 발생 시 실패 화면으로 데이터 전달
    private String renderFailWithMsg(String message, String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "fail";
    }
}