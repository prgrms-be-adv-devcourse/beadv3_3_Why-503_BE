package io.why503.paymentservice.domain.payment.controller;

import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import io.why503.paymentservice.domain.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;

/**
 * 결제 화면 컨트롤러 (View)
 * - 사용자가 보는 HTML 페이지(Thymeleaf)를 반환합니다.
 */
@Controller
@RequiredArgsConstructor
public class PaymentViewController {

    private final BookingRepository bookingRepository;

    @Value("${payment.client-key}")
    private String clientKey;

    /**
     * 결제 페이지 (Checkout)
     * - 토스 페이먼츠 위젯이 렌더링될 페이지입니다.
     */
    @GetMapping("/payment/checkout")
    public String checkoutPage(@RequestParam Long bookingSq, Model model) {
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다."));

        // 대표 티켓 정보 추출
        Ticket ticket = booking.getTickets().get(0);

        model.addAttribute("amount", booking.getPgAmount());
        model.addAttribute("orderId", booking.getOrderId());
        model.addAttribute("productName", ticket.getShowName());
        model.addAttribute("clientKey", clientKey);

        // 부가 정보 (날짜 포맷팅)
        String formattedDate = ticket.getRoundDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        model.addAttribute("showDate", formattedDate);
        model.addAttribute("seatGrade", ticket.getGrade());
        model.addAttribute("seatArea", ticket.getSeatArea());

        return "index";
    }

    /**
     * 결제 성공 페이지
     */
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

    /**
     * 결제 실패 페이지
     */
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