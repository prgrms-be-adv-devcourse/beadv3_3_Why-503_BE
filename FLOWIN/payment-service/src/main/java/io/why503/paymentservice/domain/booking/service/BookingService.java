package io.why503.paymentservice.domain.booking.service;

import io.why503.paymentservice.domain.booking.model.dto.request.BookingRequest;
import io.why503.paymentservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.paymentservice.domain.booking.model.entity.Booking;

import java.util.List;

/**
 * 예매 생성, 조회, 취소 등 예매 프로세스 전반의 비즈니스 로직을 정의하는 인터페이스
 */
public interface BookingService {

    // 새로운 예매 정보 생성
    BookingResponse createBooking(Long userSq, BookingRequest request);

    // 예매 상세 내역 조회
    BookingResponse findBooking(Long userSq, Long bookingSq);

    // 사용자의 전체 예매 목록 조회
    List<BookingResponse> findBookingsByUser(Long userSq);

    // 전체 또는 일부 티켓에 대한 예매 취소 처리
    BookingResponse cancelBooking(Long userSq, Long bookingSq, List<Long> ticketSqs, String reason);

    // 일정 시간 동안 결제되지 않은 예매 건 일괄 취소
    int cancelExpiredBookings(int expirationMinutes);

    // 주문 식별자를 통한 예매 엔티티 조회
    Booking findByOrderId(String orderId);
}