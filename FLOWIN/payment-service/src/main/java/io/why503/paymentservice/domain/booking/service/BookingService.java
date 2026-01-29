package io.why503.paymentservice.domain.booking.service;

import io.why503.paymentservice.domain.booking.model.dto.request.BookingRequest;
import io.why503.paymentservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.paymentservice.domain.booking.model.entity.Booking;

import java.util.List;

/**
 * 예매 서비스 인터페이스
 * - 외부(Controller, 타 서비스)에서 사용할 예매 관련 핵심 기능을 정의합니다.
 */
public interface BookingService {

    // 예매 생성
    BookingResponse createBooking(BookingRequest bookingRequest, Long userSq);

    // 포인트 적용
    BookingResponse applyPointToBooking(Long bookingSq, Long userSq, Long pointToUse);

    // 예매 확정 (결제 완료 후)
    void confirmBooking(Long bookingSq, String paymentKey, String paymentMethod, Long userSq);

    // 예매 전체 취소
    void cancelBooking(Long bookingSq, Long userSq);

    // 티켓 부분 취소
    void cancelTicket(Long bookingSq, Long ticketSq, Long userSq);

    // 만료 예매 자동 취소 (스케줄러용)
    int cancelExpiredBookings(int expirationMinutes);

    // 예매 단건 조회
    BookingResponse getBooking(Long bookingSq, Long userSq);

    // 사용자별 예매 목록 조회
    List<BookingResponse> getBookingsByUser(Long userSq);

    // QR 입장 처리
    void enterTicket(String ticketUuid);

    Booking getMyBooking(Long bookingSq, Long userSq);
}