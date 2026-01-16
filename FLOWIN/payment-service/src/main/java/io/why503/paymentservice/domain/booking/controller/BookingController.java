package io.why503.paymentservice.domain.booking.controller;

import io.why503.paymentservice.domain.booking.model.dto.BookingReq;
import io.why503.paymentservice.domain.booking.model.dto.BookingRes;
import io.why503.paymentservice.domain.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings") // 복수형 URL 권장
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // 예매 생성
    @PostMapping
    public ResponseEntity<BookingRes> createBooking(@RequestBody BookingReq req) {
        return ResponseEntity.ok(bookingService.createBooking(req));
    }

    // 예매 상세 조회
    @GetMapping("/{bookingSq}")
    public ResponseEntity<BookingRes> getBooking(@PathVariable Long bookingSq) {
        return ResponseEntity.ok(bookingService.getBooking(bookingSq));
    }

    // 예매 취소
    @PatchMapping("/{bookingSq}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingSq) {
        bookingService.cancelBooking(bookingSq);
        return ResponseEntity.ok().build();
    }

    // 예매 확정 API
    // 요청 예시: PATCH /bookings/1/confirm?paymentKey=toss_1234
    @PatchMapping("/{bookingSq}/confirm")
    public ResponseEntity<Void> confirmBooking(
            @PathVariable Long bookingSq,
            @RequestParam String paymentKey
    ) {
        bookingService.confirmBooking(bookingSq, paymentKey);
        return ResponseEntity.ok().build();
    }

    // 개별 티켓 취소 API
    @PatchMapping("/{bookingSq}/tickets/{ticketSq}/cancel")
    public ResponseEntity<Void> cancelTicket(
            @PathVariable Long bookingSq,
            @PathVariable Long ticketSq
    ) {
        bookingService.cancelTicket(bookingSq, ticketSq);
        return ResponseEntity.ok().build();
    }
}
