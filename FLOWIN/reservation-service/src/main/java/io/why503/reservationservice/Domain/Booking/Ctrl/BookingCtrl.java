package io.why503.reservationservice.Domain.Booking.Ctrl;

import io.why503.reservationservice.Domain.Booking.Model.Dto.BookingRequestDto;
import io.why503.reservationservice.Domain.Booking.Sv.BookingSv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingCtrl {

    private final BookingSv bookingSv;

    // 1. 직접 좌석 선택 API (Body 사용)
    @PostMapping("/direct")
    public ResponseEntity<Map<String, Object>> createDirectBooking(@RequestBody BookingRequestDto request) {
        Long bookingId = bookingSv.createDirectBooking(request.getUserSq(), request.getSeatSq());
        return ResponseEntity.ok(createSuccessResponse(bookingId, "직접 좌석 선택 성공"));
    }

    // 2. 빠른 좌석 선택 API (Body 사용)
    @PostMapping("/quick")
    public ResponseEntity<Map<String, Object>> createQuickBooking(@RequestBody BookingRequestDto request) {
        Long bookingId = bookingSv.createQuickBooking(request.getUserSq(), request.getShowingSq(), request.getGrade());
        return ResponseEntity.ok(createSuccessResponse(bookingId, "빠른 좌석 선택(랜덤) 성공"));
    }

    private Map<String, Object> createSuccessResponse(Long bookingId, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("bookingId", bookingId);
        response.put("nextStep", "할인 쿠폰 선택 및 결제 페이지로 이동");
        return response;
    }

    @PostMapping("/{bookingId}/coupon")
    public ResponseEntity<Map<String, Object>> applyCoupon(
            @PathVariable Long bookingId,
            @RequestParam Long couponId
    ) {
        Map<String, Object> result = bookingSv.applyCoupon(bookingId, couponId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{bookingId}/payment-step")
    public ResponseEntity<Map<String, Object>> getPaymentStep(@PathVariable Long bookingId) {
        Map<String, Object> paymentData = bookingSv.getPaymentInfo(bookingId);
        return ResponseEntity.ok(paymentData);
    }

    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<String> confirmBooking(@PathVariable Long bookingId) {
        bookingSv.confirmBooking(bookingId);
        return ResponseEntity.ok("결제가 확정되었습니다. 즐거운 관람 되세요!");
    }

    /**
     * [API] 예매 취소 요청
     * DELETE /bookings/{bookingId}/cancel
     */
    @DeleteMapping("/{bookingId}/cancel")
    public ResponseEntity<Map<String, String>> cancelBooking(@PathVariable Long bookingId) {
        // 서비스 호출
        bookingSv.cancelBooking(bookingId);

        // 성공 응답 생성
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "예매가 정상적으로 취소되었으며, 좌석이 해제되었습니다.");

        return ResponseEntity.ok(response);
    }

}