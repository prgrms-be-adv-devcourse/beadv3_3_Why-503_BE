package io.why503.performanceservice.domain.seat.model.dto;

import jakarta.validation.constraints.NotBlank;

public record SeatFindReq(
        @NotBlank(message = "좌석 번호 필요")
        Long seatNo,
        @NotBlank(message = "구역 이름 필요")
        String seatArea,
        @NotBlank(message = "공연장 id 번호 필요")
        Long concertHallId
) {
}
