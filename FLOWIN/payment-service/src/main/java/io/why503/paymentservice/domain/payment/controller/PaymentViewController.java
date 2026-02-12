package io.why503.paymentservice.domain.payment.controller;

import io.why503.paymentservice.domain.payment.config.TossPaymentConfig;
import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory;
import io.why503.paymentservice.domain.point.model.dto.response.PointResponse;
import io.why503.paymentservice.domain.point.service.PointService;
import io.why503.paymentservice.domain.ticket.model.enums.DiscountPolicy;
import io.why503.paymentservice.global.client.PerformanceClient;
import io.why503.paymentservice.global.client.ReservationClient;
import io.why503.paymentservice.global.client.dto.response.BookingResponse;
import io.why503.paymentservice.global.client.dto.response.BookingSeatResponse;
import io.why503.paymentservice.global.client.dto.response.RoundSeatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 결제 시작 단계의 정보 구성과 각 거래 결과에 따른 화면 응답을 처리하는 컨트롤러
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

    // 예매된 좌석의 원가와 할인 혜택을 대조하여 최종 결제 대상 금액을 산출하고 화면 구성
    @GetMapping("/checkout/booking/{bookingSq}")
    public String renderBookingCheckout(
            @PathVariable Long bookingSq,
            @RequestHeader("X-USER-SQ") Long userSq,
            Model model) {
        try {
            BookingResponse booking = reservationClient.getBooking(userSq, bookingSq);

            List<Long> roundSeatSqs = booking.bookingSeats().stream()
                    .map(bookingSeatResponse -> bookingSeatResponse.roundSeatSq())
                    .toList();

            List<RoundSeatResponse> seats = performanceClient.findRoundSeats(roundSeatSqs);

            if (seats == null || seats.isEmpty()) {
                throw PaymentExceptionFactory.paymentNotFound("예매된 좌석 정보를 찾을 수 없습니다.");
            }

            if (!"PENDING".equals(booking.status())) {
                return renderFailWithMsg("결제 가능한 상태가 아닙니다.", "INVALID_STATUS", model);
            }

            Map<Long, RoundSeatResponse> seatMap = seats.stream()
                    .collect(Collectors.toMap(roundSeatResponse -> roundSeatResponse.roundSeatSq(), Function.identity()));

            long totalAmount = 0;

            for (BookingSeatResponse bookingSeat : booking.bookingSeats()) {
                RoundSeatResponse seatInfo = seatMap.get(bookingSeat.roundSeatSq());

                if (seatInfo != null) {
                    long originalPrice = seatInfo.price();
                    DiscountPolicy policy = bookingSeat.discountPolicy();

                    // 각 좌석별 혜택 비율을 적용하여 실제 결제가 필요한 누적 합계 계산
                    long discountAmount = (originalPrice * policy.getDiscountPercent()) / 100;
                    totalAmount += (originalPrice - discountAmount);
                }
            }

            RoundSeatResponse firstSeat = seats.getFirst();
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

    // 포인트 충전 요청 내역과 결제 연동에 필요한 인증 정보를 조합하여 화면 출력
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

    // 대행사를 통한 결제 승인 완료 후 최종적인 거래 성공 정보 안내
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

    // 결제 과정 중 발생한 거절 사유나 오류 내용을 사용자에게 통보
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