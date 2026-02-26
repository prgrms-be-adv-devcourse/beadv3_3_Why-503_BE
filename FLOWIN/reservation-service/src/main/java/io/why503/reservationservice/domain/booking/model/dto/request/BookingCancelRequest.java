package io.why503.reservationservice.domain.booking.model.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

/**
 * 예매 취소 처리를 위한 데이터 객체
 * - 취소 대상 좌석 목록과 취소 사유를 포함
 */
public record BookingCancelRequest(
        @NotEmpty(message = "취소 좌석")
        List<Long> roundSeatSqs,

        @NotBlank(message = "취소 사유")
        String reason
) {
}