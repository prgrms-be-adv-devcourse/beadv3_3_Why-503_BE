package io.why503.paymentservice.domain.booking.controller;

import io.why503.paymentservice.domain.booking.model.dto.BookingRequest;
import io.why503.paymentservice.domain.booking.model.dto.BookingResponse;
import io.why503.paymentservice.domain.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings") // 복수형 URL 권장
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // 예매 생성
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestHeader("X-USER-SQ") Long userSq, // 헤더에서 직접 꺼냄!
            @RequestBody BookingRequest bookingRequest) {
        // 서비스로 헤더에서 꺼낸 userSq를 함께 넘겨줍니다.
        return ResponseEntity.ok(bookingService.createBooking(bookingRequest, userSq));
    }

    // 예매 상세 조회
    @GetMapping("/{bookingSq}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long bookingSq) {
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

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @RequestHeader("X-USER-SQ") Long userSq
    ) {
        // 여기서 서비스의 getBookingsByUser를 호출하면 경고가 사라집니다!
        return ResponseEntity.ok(bookingService.getBookingsByUser(userSq));
    }

}
