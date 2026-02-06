package io.why503.reservationservice.domain.booking.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 예매(좌석 선점) 생성 요청 DTO
 * - record 사용 (불변, 메서드 없음)
 * - 할인 정보는 포함하지 않음 (결제 단계에서 처리)
 */
public record BookingCreateRequest(
        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userSq,

        @NotEmpty(message = "예매할 좌석 ID 목록은 필수입니다.")
        List<Long> roundSeatSqs
) {
}