package io.why503.paymentservice.domain.booking.controller;

import io.why503.paymentservice.domain.booking.model.dto.request.ApplyPointRequest;
import io.why503.paymentservice.domain.booking.model.dto.request.BookingRequest;
import io.why503.paymentservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.paymentservice.domain.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 예매 API 컨트롤러
 * - 예매 생성, 조회, 취소, 확정 기능을 제공합니다.
 * - [수정] 모든 메서드에 사용자 검증(X-USER-SQ) 추가
 */
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // 예매 생성
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody BookingRequest bookingRequest
    ) {
        return ResponseEntity.ok(bookingService.createBooking(bookingRequest, userSq));
    }

    // 포인트 적용
    @PatchMapping("/{bookingSq}/points")
    public ResponseEntity<BookingResponse> applyPoint(
            @PathVariable Long bookingSq,
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody ApplyPointRequest request
    ) {
        return ResponseEntity.ok(bookingService.applyPointToBooking(bookingSq, userSq, request.userPoint()));
    }

    // 예매 상세 조회
    @GetMapping("/{bookingSq}")
    public ResponseEntity<BookingResponse> getBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long bookingSq
    ) {
        return ResponseEntity.ok(bookingService.getBooking(bookingSq, userSq));
    }

    // 내 예매 내역 조회
    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(@RequestHeader("X-USER-SQ") Long userSq) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userSq));
    }

    // 예매 확정
    @PatchMapping("/{bookingSq}/confirm")
    public ResponseEntity<Void> confirmBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long bookingSq,
            @RequestParam String paymentKey,
            @RequestParam String paymentMethod
    ) {
        bookingService.confirmBooking(bookingSq, paymentKey, paymentMethod, userSq);
        return ResponseEntity.ok().build();
    }

    // 예매 전체 취소
    @PatchMapping("/{bookingSq}/cancel")
    public ResponseEntity<Void> cancelBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long bookingSq
    ) {
        bookingService.cancelBooking(bookingSq, userSq);
        return ResponseEntity.ok().build();
    }

    // 티켓 개별(부분) 취소
    @PatchMapping("/{bookingSq}/tickets/{ticketSq}/cancel")
    public ResponseEntity<Void> cancelTicket(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long bookingSq,
            @PathVariable Long ticketSq
    ) {
        bookingService.cancelTicket(bookingSq, ticketSq, userSq);
        return ResponseEntity.ok().build();
    }
}