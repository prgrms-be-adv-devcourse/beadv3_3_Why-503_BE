package io.why503.reservationservice.domain.booking.controller;

import io.why503.reservationservice.domain.booking.model.dto.request.BookingCancelRequest;
import io.why503.reservationservice.domain.booking.model.dto.request.BookingCreateRequest;
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

    /**
     * 예매 생성 (좌석 선점)
     * [POST] /bookings
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody @Valid BookingCreateRequest request) {

        validateUserHeader(userSq);
        BookingResponse response = bookingService.createBooking(userSq, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 내 예매 목록 조회
     * [GET] /bookings
     */
    @GetMapping
    public ResponseEntity<List<BookingResponse>> findMyBookings(
            @RequestHeader("X-USER-SQ") Long userSq) {

        validateUserHeader(userSq);
        return ResponseEntity.ok(bookingService.findBookingsByUser(userSq));
    }

    /**
     * 예매 상세 조회
     * [GET] /bookings/{bookingSq}
     */
    @GetMapping("/{bookingSq}")
    public ResponseEntity<BookingResponse> findBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long bookingSq) {

        validateUserHeader(userSq);
        return ResponseEntity.ok(bookingService.findBooking(userSq, bookingSq));
    }

    /**
     * [추가] 주문 번호(OrderId)로 예매 상세 조회
     * - PaymentService에서 결제 요청 시 해당 주문 번호의 유효성을 검증하기 위해 사용
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<BookingResponse> findBookingByOrderId(@PathVariable String orderId) {
        // Service 내부에서 존재 여부 체크 후 BookingResponse(DTO) 반환
        return ResponseEntity.ok(bookingService.findBookingByOrderId(orderId));
    }

    /**
     * 예매 취소 (전체 또는 부분)
     * [POST] /bookings/{bookingSq}/cancel
     * - DELETE 메서드 대신 POST를 사용하여 명시적인 취소 행위(Action)를 표현하고 Body 사용을 용이하게 함
     */
    @PostMapping("/{bookingSq}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long bookingSq,
            @RequestBody(required = false) BookingCancelRequest request) {

        validateUserHeader(userSq);

        List<Long> seats = (request != null) ? request.roundSeatSqs() : null;
        String reason = (request != null) ? request.reason() : "사용자 요청에 의한 취소";

        return ResponseEntity.ok(bookingService.cancelBooking(userSq, bookingSq, seats, reason));
    }

    /**
     * [Internal] 결제 완료 처리 (Payment Service에서 호출)
     * - 상태를 PENDING -> PAID로 변경
     */
    @PostMapping("/{bookingSq}/paid")
    public ResponseEntity<Void> confirmPaid(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long bookingSq) {

        validateUserHeader(userSq);
        bookingService.confirmPaid(userSq, bookingSq);
        return ResponseEntity.ok().build();
    }

    /**
     * [Internal] 결제 후 환불에 따른 좌석 해제 요청
     */
    @PostMapping("/{bookingSq}/refund")
    public ResponseEntity<Void> refundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long bookingSq,
            @RequestBody List<Long> roundSeatSqs) {

        validateUserHeader(userSq);
        bookingService.refundSeats(userSq, bookingSq, roundSeatSqs);
        return ResponseEntity.ok().build();
    }

    // --- Internal / Admin 용 엔드포인트가 필요하다면 여기에 추가 (현재는 생략) ---

    private void validateUserHeader(Long userSq) {
        if (userSq == null || userSq <= 0) {
            throw BookingExceptionFactory.bookingBadRequest("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }
    }
}