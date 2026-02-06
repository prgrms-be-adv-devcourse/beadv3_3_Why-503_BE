package io.why503.reservationservice.domain.booking.controller;

import io.why503.reservationservice.domain.booking.model.dto.request.BookingCancelRequest;
import io.why503.reservationservice.domain.booking.model.dto.request.BookingRequest;
import io.why503.reservationservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.reservationservice.domain.booking.service.BookingService;
import io.why503.reservationservice.domain.booking.util.BookingExceptionFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 예매 생성, 조회 및 취소 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // 새로운 예매 생성 요청 처리
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody @Valid BookingRequest request) {

        validateUserHeader(userSq);

        BookingResponse response = bookingService.createBooking(userSq, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 예매 상세 내역 조회
    @GetMapping("/{bookingSq}")
    public ResponseEntity<BookingResponse> findBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable("bookingSq") Long bookingSq) {

        validateUserHeader(userSq);

        BookingResponse response = bookingService.findBooking(userSq, bookingSq);
        return ResponseEntity.ok(response);
    }

    // 사용자의 전체 예매 목록 조회
    @GetMapping
    public ResponseEntity<List<BookingResponse>> findBookings(
            @RequestHeader("X-USER-SQ") Long userSq) {

        validateUserHeader(userSq);

        List<BookingResponse> responses = bookingService.findBookingsByUser(userSq);
        return ResponseEntity.ok(responses);
    }

    // 기존 예매의 전체 또는 부분 취소 처리
    @PostMapping("/{bookingSq}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable("bookingSq") Long bookingSq,
            @RequestBody @Valid BookingCancelRequest request) {

        validateUserHeader(userSq);

        BookingResponse response = bookingService.cancelBooking(
                userSq,
                bookingSq,
                request.ticketSqs(),
                request.reason()
        );
        return ResponseEntity.ok(response);
    }

    // 요청 헤더의 사용자 식별값 유효성 검증
    private void validateUserHeader(Long userSq) {
        if (userSq == null || userSq <= 0) {
            throw BookingExceptionFactory.bookingBadRequest("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }
    }
}