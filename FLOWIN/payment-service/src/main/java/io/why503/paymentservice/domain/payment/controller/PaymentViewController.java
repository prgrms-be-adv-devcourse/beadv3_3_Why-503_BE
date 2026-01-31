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
 * 결제 관련 뷰를 제어하는 통합 컨트롤러
 */
@Controller
@RequestMapping("/payment-view")
@RequiredArgsConstructor
public class PaymentViewController {

    private final BookingService bookingService;
    private final PointService pointService;
    private final TossPaymentConfig tossPaymentConfig;

    // 티켓 예매 결제 진입 (체크아웃)
    @GetMapping("/checkout/booking/{bookingSq}")
    public String renderBookingCheckout(@PathVariable Long bookingSq, Model model) {
        // [보완] 유효성 검사 실패 시 메시지 전달
        if (bookingSq == null || bookingSq <= 0) {
            return this.renderFailWithMsg("유효하지 않은 예매 요청입니다.", "INVALID_REQUEST", model);
        }

        // 실제 서비스 메서드 호출
        BookingResponse booking = bookingService.findBooking(1L, bookingSq);

        // [보완] 조회 실패 시 처리
        if (booking == null) {
            return this.renderFailWithMsg("예매 정보를 찾을 수 없습니다.", "BOOKING_NOT_FOUND", model);
        }

        model.addAttribute("clientKey", tossPaymentConfig.getClientKey());
        model.addAttribute("orderId", booking.orderId());
        model.addAttribute("amount", booking.finalAmount());

        // [보완] Java 버전 호환성을 위해 get(0) 사용 (티켓이 있을 때만)
        if (booking.tickets() != null && !booking.tickets().isEmpty()) {
            var firstTicket = booking.tickets().get(0);
            model.addAttribute("productName", firstTicket.showName());
            model.addAttribute("showDate", firstTicket.roundDt());
            model.addAttribute("seatGrade", firstTicket.seatGrade());
            model.addAttribute("seatArea", firstTicket.seatArea() + " " + firstTicket.seatAreaNum());
        } else {
            // 티켓 정보가 없는 예외적 상황 대비
            model.addAttribute("productName", "공연 예매");
            model.addAttribute("showDate", "-");
            model.addAttribute("seatGrade", "-");
            model.addAttribute("seatArea", "-");
        }

        return "checkout";
    }

    // 포인트 충전 결제 진입
    @GetMapping("/checkout/point/{pointSq}")
    public String renderPointCheckout(@PathVariable Long pointSq, Model model) {
        if (pointSq == null || pointSq <= 0) {
            return this.renderFailWithMsg("유효하지 않은 충전 요청입니다.", "INVALID_REQUEST", model);
        }

        PointResponse point = pointService.findPoint(1L, pointSq);

        if (point == null) {
            return this.renderFailWithMsg("충전 정보를 찾을 수 없습니다.", "POINT_NOT_FOUND", model);
        }

        model.addAttribute("clientKey", tossPaymentConfig.getClientKey());
        model.addAttribute("orderId", point.orderId());
        model.addAttribute("amount", point.chargeAmount());
        model.addAttribute("productName", "포인트 충전");

        // 포인트 충전 시 불필요한 정보 숨김 처리용 값
        model.addAttribute("showDate", "-");
        model.addAttribute("seatGrade", "POINT");
        model.addAttribute("seatArea", "CHARGE");

        return "checkout";
    }

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

        // [보완] success.html에 표시할 상품명 추론 (DB 조회 없이 orderId로 판단)
        String productName = "결제 상품";
        if (orderId.startsWith("BOOKING")) {
            productName = "공연 예매"; // 상세 공연명은 DB 조회 필요하므로 범용적 명칭 사용
        } else if (orderId.startsWith("POINT")) {
            productName = "포인트 충전";
        }
        model.addAttribute("productName", productName);

        return "success";
    }

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

    // [보완] 내부적으로 사용하는 실패 뷰 헬퍼 메서드
    private String renderFailWithMsg(String message, String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "fail";
    }
}