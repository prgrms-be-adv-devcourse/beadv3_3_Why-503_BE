package io.why503.paymentservice.domain.payment.controller;

import io.why503.paymentservice.domain.payment.config.TossPaymentConfig;
import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory;
import io.why503.paymentservice.domain.point.model.dto.response.PointResponse;
import io.why503.paymentservice.domain.point.service.PointService;
import io.why503.paymentservice.global.client.PerformanceClient;
import io.why503.paymentservice.global.client.ReservationClient;
import io.why503.paymentservice.global.client.dto.response.BookingResponse;
import io.why503.paymentservice.global.client.dto.response.RoundSeatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 결제 진입점 및 결과 화면 렌더링을 담당하는 뷰 컨트롤러
 * - 예매 및 포인트 충전 체크아웃 페이지 제공
 */
@Slf4j
@Controller
@RequestMapping("/payment-view")
@RequiredArgsConstructor
public class PaymentViewController {

    private final ReservationClient reservationClient;
    private final PerformanceClient performanceClient;
    private final PointService pointService;
    private final TossPaymentConfig tossPaymentConfig;

    // 예매된 좌석 상세 정보와 결제 금액을 포함한 체크아웃 화면 렌더링
    @GetMapping("/checkout/booking/{bookingSq}")
    public String renderBookingCheckout(
            @PathVariable Long bookingSq,
            @RequestHeader("X-USER-SQ") Long userSq,
            Model model) {
        try {
            BookingResponse booking = reservationClient.getBooking(userSq, bookingSq);
            List<RoundSeatResponse> seats = performanceClient.findRoundSeats(booking.roundSeatSqs());

            if (seats == null || seats.isEmpty()) {
                throw PaymentExceptionFactory.paymentNotFound("예매된 좌석 정보를 찾을 수 없습니다.");
            }

            if (!"PENDING".equals(booking.status())) {
                return renderFailWithMsg("결제 가능한 상태가 아닙니다. (현재 상태: " + booking.status() + ")", "INVALID_STATUS", model);
            }

            RoundSeatResponse firstSeat = seats.get(0);

            model.addAttribute("productName", firstSeat.showName());

            String dateInfo = firstSeat.roundDt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            model.addAttribute("showDate", dateInfo);

            String seatGrade = firstSeat.grade() + "석";
            String seatArea;

            if (seats.size() > 1) {
                seatArea = String.format("%s구역 %d번 외 %d석",
                        firstSeat.seatArea(), firstSeat.seatAreaNum(), seats.size() - 1);
            } else {
                seatArea = String.format("%s구역 %d번",
                        firstSeat.seatArea(), firstSeat.seatAreaNum());
            }

            model.addAttribute("seatGrade", seatGrade);
            model.addAttribute("seatArea", seatArea);

            long totalAmount = seats.stream()
                    .mapToLong(roundSeatResponse -> roundSeatResponse.price())
                    .sum();
            model.addAttribute("amount", totalAmount);

            model.addAttribute("orderId", booking.orderId());
            model.addAttribute("clientKey", tossPaymentConfig.getClientKey());
            model.addAttribute("customerKey", "USER-" + userSq);

            return "checkout";

        } catch (Exception e) {
            log.error("예매 체크아웃 렌더링 실패", e);
            return renderFailWithMsg(e.getMessage(), "CHECKOUT_ERROR", model);
        }
    }

    // 포인트 충전 금액 및 결제 식별 정보를 포함한 체크아웃 화면 렌더링
    @GetMapping("/checkout/point/{pointSq}")
    public String renderPointCheckout(
            @PathVariable Long pointSq,
            @RequestHeader("X-USER-SQ") Long userSq,
            Model model) {
        try {
            PointResponse point = pointService.findPoint(userSq, pointSq);

            model.addAttribute("productName", "포인트 충전");
            model.addAttribute("showDate", "-");
            model.addAttribute("seatGrade", "POINT");
            model.addAttribute("seatArea", "-");

            model.addAttribute("amount", point.chargeAmount());
            model.addAttribute("orderId", point.orderId());
            model.addAttribute("clientKey", tossPaymentConfig.getClientKey());
            model.addAttribute("customerKey", "USER-" + userSq);

            return "checkout";

        } catch (Exception e) {
            log.error("포인트 체크아웃 렌더링 실패", e);
            return renderFailWithMsg(e.getMessage(), "CHECKOUT_ERROR", model);
        }
    }

    // 결제 성공 후 리다이렉트되어 결제 승인 결과를 보여주는 페이지
    @GetMapping("/success")
    public String renderSuccessPage(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            Model model) {

        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);

        String productName = "결제 상품";
        if (orderId.startsWith("BOOKING")) productName = "공연 예매";
        else if (orderId.startsWith("POINT")) productName = "포인트 충전";

        model.addAttribute("productName", productName);

        return "success";
    }

    // 결제 과정에서 발생한 오류 메시지를 사용자에게 안내하는 페이지
    @GetMapping("/fail")
    public String renderFailPage(
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String orderId,
            Model model) {

        model.addAttribute("message", message != null ? message : "결제에 실패했습니다.");
        model.addAttribute("code", code != null ? code : "UNKNOWN_ERROR");
        model.addAttribute("orderId", orderId);

        return "fail";
    }

    private String renderFailWithMsg(String message, String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "fail";
    }
}