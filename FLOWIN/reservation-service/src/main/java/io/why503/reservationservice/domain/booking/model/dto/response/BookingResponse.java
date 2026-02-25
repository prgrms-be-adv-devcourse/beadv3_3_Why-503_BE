package io.why503.reservationservice.domain.booking.model.dto.response;


/**
 * 예매 상세 및 상태 정보를 반환하는 데이터 객체
 * - 예매 식별자, 상태, 선점된 좌석 목록을 포함
 */
public record BookingResponse(
        Long sq,
        Long userSq,
        String status,
        String category,
        String genre
) {
}