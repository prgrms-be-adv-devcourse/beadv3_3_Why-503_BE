package io.why503.paymentservice.domain.payment.controller;

import io.why503.paymentservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.paymentservice.domain.booking.model.dto.response.TicketResponse;
import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import io.why503.paymentservice.domain.booking.service.BookingService;
import io.why503.paymentservice.domain.payment.config.TossPaymentConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;

/**
 * 결제 화면 컨트롤러 (View)
 * - 사용자가 보는 HTML 페이지(Thymeleaf)를 반환합니다.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/payment-view")
public class PaymentViewController {

    private final BookingService bookingService;
    private final TossPaymentConfig tossPaymentConfig;

    // 결제 페이지 (Checkout)
    @GetMapping("/checkout")
    public String checkoutPage(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestParam Long bookingSq,
            Model model
    ) {
        // 1. [수정] DTO로 받기 (Entity X) -> 트랜잭션 범위 밖에서도 안전함
        BookingResponse booking = bookingService.getBooking(bookingSq, userSq);

        // 2. [수정] 티켓 정보 추출 (Record는 Getter가 필드명과 동일)
        if (booking.tickets() == null || booking.tickets().isEmpty()) {
            throw new IllegalStateException("예매에 포함된 티켓이 없습니다.");
        }
        TicketResponse ticket = booking.tickets().get(0); // List의 첫 번째 요소

        // 3. 모델 담기 (Record 스타일: .getPgAmount() -> .pgAmount())
        model.addAttribute("amount", booking.pgAmount());
        model.addAttribute("orderId", booking.orderId()); // DTO에 orderId 필드가 없다면 추가 필요! (*)
        model.addAttribute("productName", ticket.showName());
        model.addAttribute("clientKey", tossPaymentConfig.getClientKey()); // Config에서 가져오기

        // 4. 날짜 포맷팅
        String formattedDate = ticket.roundDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        model.addAttribute("showDate", formattedDate);
        model.addAttribute("seatGrade", ticket.grade());
        model.addAttribute("seatArea", ticket.seatArea());

        return "checkout"; // 보통 결제 페이지는 index보단 checkout 같은 이름이 명확합니다.
    }

    // 결제 성공 페이지
    @GetMapping("/success")
    public String successPage(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            Model model
    ) {
        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        return "success";
    }

    // 결제 실패 페이지
    @GetMapping("/fail")
    public String failPage(
            @RequestParam String message,
            @RequestParam String code,
            @RequestParam(required = false) String orderId,
            Model model
    ) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        model.addAttribute("orderId", orderId);
        return "fail";
    }
}