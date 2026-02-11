package io.why503.reservationservice.domain.booking.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 예매 확정 전 선택한 좌석들에 대해 일괄적으로 할인 정책 적용을 요청하는 객체
 */
public record BookingDiscountRequest(
        @NotEmpty(message = "할인 적용할 좌석 정보가 없습니다.")
        @Valid
        List<BookingDiscountSeatRequest> seats
) {}