package io.why503.paymentservice.domain.booking.controller;

import io.why503.paymentservice.domain.booking.model.dto.BookingRequest;
import io.why503.paymentservice.domain.booking.model.dto.BookingResponse;
import io.why503.paymentservice.domain.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 예매 API 컨트롤러
 * - 예매 생성, 조회, 취소, 확정 기능을 제공합니다.
 */
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * 예매 생성
     * - 결제 전 'PENDING' 상태의 예매 데이터를 생성합니다.
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody BookingRequest bookingRequest
    ) {
        return ResponseEntity.ok(bookingService.createBooking(bookingRequest, userSq));
    }

    /**
     * 예매 상세 조회
     */
    @GetMapping("/{bookingSq}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long bookingSq) {
        return ResponseEntity.ok(bookingService.getBooking(bookingSq));
    }

    /**
     * 내 예매 내역 조회
     */
    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(@RequestHeader("X-USER-SQ") Long userSq) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userSq));
    }

    /**
     * 예매 확정
     * - PG사 결제 승인 후 호출되며, 예매 상태를 'CONFIRMED'로 변경합니다.
     */
    @PatchMapping("/{bookingSq}/confirm")
    public ResponseEntity<Void> confirmBooking(
            @PathVariable Long bookingSq,
            @RequestParam String paymentKey,
            @RequestParam String paymentMethod
    ) {
        bookingService.confirmBooking(bookingSq, paymentKey, paymentMethod);
        return ResponseEntity.ok().build();
    }

    /**
     * 예매 전체 취소
     */
    @PatchMapping("/{bookingSq}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingSq) {
        bookingService.cancelBooking(bookingSq);
        return ResponseEntity.ok().build();
    }

    /**
     * 티켓 개별(부분) 취소
     */
    @PatchMapping("/{bookingSq}/tickets/{ticketSq}/cancel")
    public ResponseEntity<Void> cancelTicket(
            @PathVariable Long bookingSq,
            @PathVariable Long ticketSq
    ) {
        bookingService.cancelTicket(bookingSq, ticketSq);
        return ResponseEntity.ok().build();
    }
}