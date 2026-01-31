package io.why503.paymentservice.domain.booking.controller;

import io.why503.paymentservice.domain.booking.model.dto.request.BookingCancelRequest;
import io.why503.paymentservice.domain.booking.model.dto.request.BookingRequest;
import io.why503.paymentservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.paymentservice.domain.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * 예매 생성
     * POST /bookings
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody @Valid BookingRequest request) {

        validateUserHeader(userSq); // 검증 로직 메서드 추출로 가독성 향상

        BookingResponse response = bookingService.createBooking(userSq, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 예매 상세 조회
     * GET /bookings/{bookingSq}
     */
    @GetMapping("/{bookingSq}")
    public ResponseEntity<BookingResponse> findBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable("bookingSq") Long bookingSq) {

        validateUserHeader(userSq);

        BookingResponse response = bookingService.findBooking(userSq, bookingSq);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 예매 목록 조회
     * GET /bookings
     */
    @GetMapping
    public ResponseEntity<List<BookingResponse>> findBookings(
            @RequestHeader("X-USER-SQ") Long userSq) {

        validateUserHeader(userSq);

        List<BookingResponse> responses = bookingService.findBookingsByUser(userSq);
        return ResponseEntity.ok(responses);
    }

    /**
     * 예매 취소 (부분/전체)
     * POST /bookings/{bookingSq}/cancel
     */
    @PostMapping("/{bookingSq}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable("bookingSq") Long bookingSq,
            @RequestBody @Valid BookingCancelRequest request) {

        validateUserHeader(userSq);

        // [수정됨] request.reason() 검증 로직 제거 -> @Valid가 처리함

        BookingResponse response = bookingService.cancelBooking(
                userSq,
                bookingSq,
                request.ticketSqs(),
                request.reason()
        );
        return ResponseEntity.ok(response);
    }

    // 헤더 검증용 Private 메서드 (중복 코드 제거)
    private void validateUserHeader(Long userSq) {
        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }
    }
}