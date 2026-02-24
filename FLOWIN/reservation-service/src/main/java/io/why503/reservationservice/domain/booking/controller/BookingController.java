package io.why503.reservationservice.domain.booking.controller;

import io.why503.reservationservice.domain.booking.model.dto.request.BookingCancelRequest;
import io.why503.reservationservice.domain.booking.model.dto.request.BookingCreateRequest;
import io.why503.reservationservice.domain.booking.model.dto.request.BookingDiscountRequest;
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
 * 예매(좌석 선점) 관리 컨트롤러
 * - 사용자의 예매 요청, 조회, 취소를 처리
 * - Gateway에서 넘어오는 X-USER-SQ 헤더를 필수값으로 사용
 */
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // 신규 예매를 통한 좌석 점유 요청
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody @Valid BookingCreateRequest request) {

        validateUserHeader(userSq);
        BookingResponse response = bookingService.createBooking(userSq, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 선점된 예매 건에 좌석별 할인 정책 적용
    @PatchMapping("/{bookingSq}/discount")
    public ResponseEntity<BookingResponse> applyDiscounts(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long bookingSq,
            @RequestBody @Valid BookingDiscountRequest request) {

        validateUserHeader(userSq);
        BookingResponse response = bookingService.applyDiscounts(userSq, bookingSq, request);
        return ResponseEntity.ok(response);
    }

    // 본인의 전체 예매 이력 조회
    @GetMapping
    public ResponseEntity<List<BookingResponse>> findMyBookings(
            @RequestHeader("X-USER-SQ") Long userSq) {

        validateUserHeader(userSq);
        return ResponseEntity.ok(bookingService.findBookingsByUser(userSq));
    }

    @GetMapping("/{bookingSq}")
    public ResponseEntity<BookingResponse> findBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long bookingSq) {

        validateUserHeader(userSq);
        return ResponseEntity.ok(bookingService.findBooking(userSq, bookingSq));
    }

    // 결제 시스템의 데이터 대조 및 유효성 검증 목적
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<BookingResponse> findBookingByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(bookingService.findBookingByOrderId(orderId));
    }

    // 예매 철회 및 부분 좌석 해제
    @PostMapping("/{bookingSq}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long bookingSq,
            @RequestBody(required = false) @Valid BookingCancelRequest request) {

        validateUserHeader(userSq);

        List<Long> seats = (request != null) ? request.roundSeatSqs() : null;
        String reason = (request != null) ? request.reason() : "사용자 요청에 의한 취소";

        return ResponseEntity.ok(bookingService.cancelBooking(userSq, bookingSq, seats, reason));
    }

    // 결제 확정 시 예매 상태를 실결제 완료로 전환
    @PostMapping("/{bookingSq}/paid")
    public ResponseEntity<Void> confirmPaid(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long bookingSq) {

        validateUserHeader(userSq);
        bookingService.confirmPaid(userSq, bookingSq);
        return ResponseEntity.ok().build();
    }

    // 환불 처리에 따른 점유 좌석 복구
    @PostMapping("/{bookingSq}/refund")
    public ResponseEntity<Void> refundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long bookingSq,
            @RequestBody List<Long> roundSeatSqs) {

        validateUserHeader(userSq);
        bookingService.refundSeats(userSq, bookingSq, roundSeatSqs);
        return ResponseEntity.ok().build();
    }

    // 필수 사용자 정보 헤더 존재 여부 확인
    private void validateUserHeader(Long userSq) {
        if (userSq == null || userSq <= 0) {
            throw BookingExceptionFactory.bookingBadRequest("유효하지 않은 사용자입니다.");
        }
    }
}