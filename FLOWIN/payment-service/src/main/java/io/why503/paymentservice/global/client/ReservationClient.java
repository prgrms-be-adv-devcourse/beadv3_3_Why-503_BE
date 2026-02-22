package io.why503.paymentservice.global.client;

import io.why503.paymentservice.global.client.dto.response.BookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 예매 서비스와의 통신을 통해 예약 상태를 조회하고 결제 결과를 동기화하는 클라이언트
 */
@FeignClient(name = "reservation-service", url = "http://reservation-account:8400")
public interface ReservationClient {

    // 예매 식별자를 통한 예약 상세 정보 및 점유 좌석 현황 조회
    @GetMapping("/bookings/{bookingSq}")
    BookingResponse getBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable("bookingSq") Long bookingSq
    );

    // 주문 식별자를 기반으로 결제 대상 예약 건의 유효성 검증을 위한 데이터 추출
    @GetMapping("/bookings/orders/{orderId}")
    BookingResponse findBookingByOrderId(
            @PathVariable("orderId") String orderId
    );

    // 결제 승인 완료에 따른 예약 상태 확정 요청
    @PostMapping("/bookings/{bookingSq}/paid")
    void confirmPaid(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable("bookingSq") Long bookingSq
    );

    // 결제 취소 또는 환불 시 점유 중인 좌석의 권한 해제 처리
    @PostMapping("/bookings/{bookingSq}/refund")
    void refundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable("bookingSq") Long bookingSq,
            @RequestBody List<Long> roundSeatSqs
    );
}