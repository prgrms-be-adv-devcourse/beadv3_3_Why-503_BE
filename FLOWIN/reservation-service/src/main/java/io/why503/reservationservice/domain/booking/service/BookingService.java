package io.why503.reservationservice.domain.booking.service;

import io.why503.reservationservice.domain.booking.model.dto.request.BookingCreateRequest;
import io.why503.reservationservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.reservationservice.domain.booking.model.entity.Booking;

import java.util.List;

/**
 * 예매 생성, 조회, 취소 등 예매 프로세스 전반의 비즈니스 로직을 정의하는 인터페이스
 * - 기존 PaymentService의 구조를 유지하되, Ticket 관련 로직을 BookingSeat/RoundSeat 기준으로 변경
 */
public interface BookingService {

    /**
     * 새로운 예매 정보 생성 (좌석 선점)
     * @param userSq 사용자 ID
     * @param request 예매 요청 정보 (좌석 목록 포함)
     * @return 생성된 예매 정보
     */
    BookingResponse createBooking(Long userSq, BookingCreateRequest request);

    /**
     * 예매 상세 내역 조회
     * @param userSq 사용자 ID (권한 검증용)
     * @param bookingSq 예매 ID
     * @return 예매 상세 정보
     */
    BookingResponse findBooking(Long userSq, Long bookingSq);

    /**
     * 사용자의 전체 예매 목록 조회
     * @param userSq 사용자 ID
     * @return 예매 목록
     */
    List<BookingResponse> findBookingsByUser(Long userSq);

    /**
     * 전체 또는 일부 좌석에 대한 예매 취소 처리
     * - 기존 ticketSqs -> roundSeatSqs로 변경 (Ticket 엔티티 부재)
     * @param userSq 사용자 ID
     * @param bookingSq 예매 ID
     * @param roundSeatSqs 취소할 회차 좌석 ID 목록 (null 또는 empty 시 전체 취소)
     * @param reason 취소 사유
     * @return 취소 후 갱신된 예매 정보
     */
    BookingResponse cancelBooking(Long userSq, Long bookingSq, List<Long> roundSeatSqs, String reason);

    /**
     * 일정 시간 동안 결제되지 않은(PENDING) 예매 건 일괄 취소
     * @param expirationMinutes 만료 기준 시간(분)
     * @return 취소된 건수
     */
    int cancelExpiredBookings(int expirationMinutes);

    /**
     * 주문 식별자를 통한 예매 엔티티 조회 (내부용)
     * @param orderId 주문 번호
     * @return Booking 엔티티
     */
    Booking findByOrderId(String orderId);

    /**
     * [추가] 주문 번호로 예매 상세 조회 (Controller용)
     * - Payment Service 등 외부 요청 대응을 위해 DTO 반환
     */
    BookingResponse findBookingByOrderId(String orderId);

    /**
     * 결제 완료 확정 처리
     * @param userSq 사용자 ID (검증용)
     * @param bookingSq 예매 ID
     */
    void confirmPaid(Long userSq, Long bookingSq); // 추가

    /**
     * 결제 후 환불에 따른 좌석 재고 해제 (Internal)
     * @param userSq 사용자 ID
     * @param bookingSq 예매 ID
     * @param roundSeatSqs 환불 대상 좌석 ID 목록
     */
    void refundSeats(Long userSq, Long bookingSq, List<Long> roundSeatSqs);
}