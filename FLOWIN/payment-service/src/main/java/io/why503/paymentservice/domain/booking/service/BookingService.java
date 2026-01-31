package io.why503.paymentservice.domain.booking.service;

import io.why503.paymentservice.domain.booking.model.dto.request.BookingRequest;
import io.why503.paymentservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.paymentservice.domain.booking.model.entity.Booking;

import java.util.List;

public interface BookingService {

    // 예매 생성
    BookingResponse createBooking(Long userSq, BookingRequest request);

    // 예매 상세 조회
    BookingResponse findBooking(Long userSq, Long bookingSq);

    // 내 예매 목록 조회
    List<BookingResponse> findBookingsByUser(Long userSq);

    // 예매 취소 (전체/부분)
    BookingResponse cancelBooking(Long userSq, Long bookingSq, List<Long> ticketSqs, String reason);

    /**
     * [추가됨] 만료된 예매 자동 취소 (스케줄러용)
     * @param expirationMinutes 만료 기준 분 (예: 10분)
     * @return 취소된 예매 건수
     */
    int cancelExpiredBookings(int expirationMinutes);

    Booking findByOrderId(String orderId);
}