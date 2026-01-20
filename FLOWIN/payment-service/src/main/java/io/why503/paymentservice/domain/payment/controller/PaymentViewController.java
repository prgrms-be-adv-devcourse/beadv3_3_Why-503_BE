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

@Controller
@RequiredArgsConstructor
public class PaymentViewController {

    private final BookingRepository bookingRepository;

    @Value("${payment.client-key}") // application.yml에 설정 필요
    private String clientKey;

    // 결제 페이지 (checkout)
    @GetMapping("/payment/checkout")
    public String checkoutPage(@RequestParam Long bookingSq, Model model) {
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다."));

        // 첫 번째 티켓 정보 가져오기
        Ticket ticket = booking.getTickets().get(0);

        model.addAttribute("amount", booking.getPgAmount());
        model.addAttribute("orderId", booking.getOrderId());
        model.addAttribute("productName", ticket.getShowName());
        model.addAttribute("clientKey", clientKey);

        // [추가된 부분] 날짜와 좌석 정보를 모델에 담습니다.
        // 날짜를 예쁘게 포맷팅 (예: 2026-01-28 00:01:05)
        String formattedDate = ticket.getRoundDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        model.addAttribute("showDate", formattedDate);     // 공연 일시
        model.addAttribute("seatGrade", ticket.getGrade()); // 좌석 등급 (VIP)
        model.addAttribute("seatArea", ticket.getSeatArea()); // 좌석 구역 (A구역)

        return "index";
    }

    // 결제 성공 페이지 (success)
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

    // 결제 실패 페이지 (fail)
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