package io.why503.paymentservice.domain.payment.controller;

import io.why503.paymentservice.domain.payment.config.TossPaymentConfig;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/payment-view")
@RequiredArgsConstructor
public class PaymentViewController {

    private final ReservationClient reservationClient;
    private final PerformanceClient performanceClient; // 좌석 상세 정보 조회를 위해 추가
    private final PointService pointService;
    private final TossPaymentConfig tossPaymentConfig;

    /**
     * [예매 결제] 체크아웃 페이지 렌더링
     */
    @GetMapping("/checkout/booking/{bookingSq}")
    public String renderBookingCheckout(
            @PathVariable Long bookingSq,
            @RequestParam(required = false, defaultValue = "1") Long userSq,
            Model model) {
        try {
            // 1. 예약 정보 조회 (MSA Feign Client)
            // BookingResponse에는 좌석 ID 목록(roundSeatSqs)만 있고, 공연명/가격 등의 상세 정보가 없음
            BookingResponse booking = reservationClient.getBooking(userSq, bookingSq);

            // 2. 좌석 상세 정보 조회 (Performance Service)
            // roundSeatSqs를 이용하여 공연명, 회차일시, 좌석등급, 가격 등을 조회
            List<RoundSeatResponse> seats = performanceClient.findRoundSeats(booking.roundSeatSqs());

            if (seats == null || seats.isEmpty()) {
                throw new IllegalStateException("예매된 좌석 정보를 찾을 수 없습니다.");
            }

            // 대표 좌석 정보 (공연명, 일시는 모든 좌석이 동일하므로 첫 번째 좌석 기준)
            RoundSeatResponse firstSeat = seats.get(0);

            // 3. HTML(Thymeleaf) 데이터 매핑
            model.addAttribute("productName", firstSeat.showName()); // 공연명

            // 날짜 포맷팅 (ex: 2024-05-20 19:30)
            String dateInfo = firstSeat.roundDt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            model.addAttribute("showDate", dateInfo);

            // 좌석 등급 및 구역 정보 구성
            // ex: "VIP석", "A구역 12번 외 1석"
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

            // 총 결제 금액 계산 (각 좌석 가격의 합)
            long totalAmount = seats.stream()
                    .mapToLong(roundSeatResponse -> roundSeatResponse.price())
                    .sum();
            model.addAttribute("amount", totalAmount);

            // 결제 필수 정보
            model.addAttribute("orderId", booking.orderId());
            model.addAttribute("clientKey", tossPaymentConfig.getClientKey());
            model.addAttribute("customerKey", "USER-" + userSq); // 토스페이먼츠 유저 식별키

            return "checkout";

        } catch (Exception e) {
            log.error("예매 체크아웃 렌더링 실패", e);
            return renderFailWithMsg(e.getMessage(), "CHECKOUT_ERROR", model);
        }
    }

    /**
     * [포인트 충전] 체크아웃 페이지 렌더링
     */
    @GetMapping("/checkout/point/{pointSq}")
    public String renderPointCheckout(
            @PathVariable Long pointSq,
            @RequestParam(required = false, defaultValue = "1") Long userSq,
            Model model) {
        try {
            // 1. 포인트 충전 정보 조회
            PointResponse point = pointService.findPoint(userSq, pointSq);

            // 2. HTML 데이터 매핑
            model.addAttribute("productName", "포인트 충전");

            // 포인트 충전은 공연 관련 정보가 없으므로 "-" 처리 (HTML에서 조건부 렌더링 가능)
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

    /**
     * 결제 성공 페이지 (Toss Payments 리다이렉트)
     */
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

    /**
     * 결제 실패 페이지 (Toss Payments 리다이렉트)
     */
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