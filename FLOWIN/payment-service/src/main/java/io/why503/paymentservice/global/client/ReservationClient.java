package io.why503.paymentservice.global.client;

import io.why503.paymentservice.global.client.dto.response.BookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 예약 서비스(Reservation Service)와 통신하는 Feign Client
 * - [땅땅땅 규칙] 결제 서비스는 예약 서비스의 상세 데이터를 이 클라이언트를 통해 가져옴
 */
@FeignClient(name = "reservation-service", url = "http://localhost:8400") // 실제 포트에 맞게 조정
public interface ReservationClient {

    /**
     * 예매 상세 조회 (PK 기반)
     * - 취소 로직 등에서 사용
     */
    @GetMapping("/bookings/{bookingSq}")
    BookingResponse getBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable("bookingSq") Long bookingSq
    );

    /**
     * [추가] 주문 번호(OrderId)로 예매 상세 조회
     * - PaymentServiceImpl.pay() 진입 시 결제 요청 검증을 위해 필수적으로 사용
     */
    @GetMapping("/bookings/orders/{orderId}")
    BookingResponse findBookingByOrderId(
            @PathVariable("orderId") String orderId
    );

    /**
     * 결제 완료 통보 (상태 변경: PENDING -> PAID)
     */
    @PostMapping("/bookings/{bookingSq}/paid")
    void confirmPaid(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable("bookingSq") Long bookingSq
    );

    /**
     * 결제 취소/환불에 따른 좌석 점유 해제 요청
     */
    @PostMapping("/bookings/{bookingSq}/refund")
    void refundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable("bookingSq") Long bookingSq,
            @RequestBody List<Long> roundSeatSqs
    );
}