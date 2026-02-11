package io.why503.reservationservice.domain.booking.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record BookingDiscountRequest(
        @NotEmpty(message = "할인 적용할 좌석 정보가 없습니다.")
        @Valid
        List<BookingDiscountSeatRequest> seats // 분리된 DTO 리스트
) {}