package io.why503.reservationservice.domain.booking.service;

import io.why503.reservationservice.domain.booking.model.dto.request.BookingCreateRequest;
import io.why503.reservationservice.domain.booking.model.dto.request.BookingDiscountRequest;
import io.why503.reservationservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.reservationservice.domain.booking.model.entity.Booking;

import java.util.List;

/**
 * 예매 데이터의 생명주기 관리와 외부 시스템 간의 좌석 점유 동기화를 정의하는 인터페이스
 */
public interface BookingService {

    // 신규 예매 기록 생성 및 타 서비스로의 좌석 선점 요청 수행
    BookingResponse createBooking(Long userSq, BookingCreateRequest request);

    // 예매된 개별 좌석에 대한 할인 정책 적용 및 결제 예정 금액 갱신
    BookingResponse applyDiscounts(Long userSq, Long bookingSq, BookingDiscountRequest request);

    BookingResponse findBooking(Long userSq, Long bookingSq);

    List<BookingResponse> findBookingsByUser(Long userSq);

    // 예매 철회에 따른 데이터 무효화 및 점유 중인 좌석 자원 방출
    BookingResponse cancelBooking(Long userSq, Long bookingSq, List<Long> roundSeatSqs, String reason);

    // 유효 결제 시간이 경과한 미결제 예매 건들의 일괄 회수 및 복구
    int cancelExpiredBookings(int expirationMinutes);

    // 주문 식별자를 통한 도메인 모델 내부 참조용 데이터 조회
    Booking findByOrderId(String orderId);

    // 외부 시스템의 요청에 대응하는 예매 상세 내역 추출
    BookingResponse findBookingByOrderId(String orderId);

    // 결제 시스템의 승인 결과를 바탕으로 예매 확정 및 발권 준비 상태 전환
    void confirmPaid(Long userSq, Long bookingSq);

    // 환불 절차 진행 시 대상 좌석들의 판매 가능 상태 복구 처리
    void refundSeats(Long userSq, Long bookingSq, List<Long> roundSeatSqs);
}