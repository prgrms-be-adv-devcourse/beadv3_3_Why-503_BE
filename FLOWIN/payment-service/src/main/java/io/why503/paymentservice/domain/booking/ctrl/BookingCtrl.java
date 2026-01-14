package io.why503.paymentservice.domain.booking.ctrl;

import io.why503.paymentservice.domain.booking.model.dto.BookingReqDto;
import io.why503.paymentservice.domain.booking.model.dto.BookingResDto;
import io.why503.paymentservice.domain.booking.sv.BookingSv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings") // 복수형 URL 권장
@RequiredArgsConstructor
public class BookingCtrl {

    private final BookingSv bookingSv;

    // 예매 생성
    @PostMapping
    public ResponseEntity<BookingResDto> createBooking(@RequestBody BookingReqDto req) {
        return ResponseEntity.ok(bookingSv.createBooking(req));
    }

    // 예매 상세 조회
    @GetMapping("/{bookingSq}")
    public ResponseEntity<BookingResDto> getBooking(@PathVariable Long bookingSq) {
        return ResponseEntity.ok(bookingSv.getBooking(bookingSq));
    }

    // 예매 취소
    @PatchMapping("/{bookingSq}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingSq) {
        bookingSv.cancelBooking(bookingSq);
        return ResponseEntity.ok().build();
    }

    // 예매 확정 API
    // 요청 예시: PATCH /bookings/1/confirm?paymentKey=toss_1234
    @PatchMapping("/{bookingSq}/confirm")
    public ResponseEntity<Void> confirmBooking(
            @PathVariable Long bookingSq,
            @RequestParam String paymentKey
    ) {
        bookingSv.confirmBooking(bookingSq, paymentKey);
        return ResponseEntity.ok().build();
    }

    // 개별 티켓 취소 API
    @PatchMapping("/{bookingSq}/tickets/{ticketSq}/cancel")
    public ResponseEntity<Void> cancelTicket(
            @PathVariable Long bookingSq,
            @PathVariable Long ticketSq
    ) {
        bookingSv.cancelTicket(bookingSq, ticketSq);
        return ResponseEntity.ok().build();
    }
}
