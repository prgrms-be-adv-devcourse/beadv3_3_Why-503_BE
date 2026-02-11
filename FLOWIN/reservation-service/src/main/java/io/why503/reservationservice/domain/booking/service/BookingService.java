package io.why503.reservationservice.domain.booking.service;

import io.why503.reservationservice.domain.booking.model.dto.request.BookingCreateRequest;
import io.why503.reservationservice.domain.booking.model.dto.request.BookingDiscountRequest;
import io.why503.reservationservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.reservationservice.domain.booking.model.entity.Booking;

import java.util.List;

/**
 * 예매 프로세스 전반의 비즈니스 로직을 정의하는 인터페이스
 * - 좌석 선점, 상태 관리, 만료 처리 및 외부 서비스 연동 로직 정의
 */
public interface BookingService {

    // 새로운 예매 정보 생성 및 좌석 선점 처리
    BookingResponse createBooking(Long userSq, BookingCreateRequest request);

    BookingResponse applyDiscounts(Long userSq, Long bookingSq, BookingDiscountRequest request);

    BookingResponse findBooking(Long userSq, Long bookingSq);

    List<BookingResponse> findBookingsByUser(Long userSq);

    // 전체 또는 일부 좌석에 대한 예매 취소 및 점유 해제
    BookingResponse cancelBooking(Long userSq, Long bookingSq, List<Long> roundSeatSqs, String reason);

    // 미결제 상태로 방치된 선점 내역의 일괄 정리
    int cancelExpiredBookings(int expirationMinutes);

    // 내부 시스템 연동을 위한 도메인 모델 조회
    Booking findByOrderId(String orderId);

    // 외부 시스템 요청에 대응하는 정보 조회
    BookingResponse findBookingByOrderId(String orderId);

    // 결제 성공 결과 반영 및 상태 확정
    void confirmPaid(Long userSq, Long bookingSq);

    // 환불 절차에 따른 좌석 재고 방출
    void refundSeats(Long userSq, Long bookingSq, List<Long> roundSeatSqs);
}