package io.why503.reservationservice.domain.booking.model.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 신규 예매 생성을 위한 데이터 객체
 * - 선점하려는 좌석 정보와 사용자 정보를 전달
 */
public record BookingCreateRequest(
        @NotEmpty(message = "예매할 좌석 ID 목록은 필수입니다.")
        List<Long> roundSeatSqs
) {
}