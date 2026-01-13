package io.why503.performanceservice.domain.concert_hall.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ConcertHallFindReq(
        @NotBlank(message = "이름 입력 필요")
        String name,
        @NotBlank(message = "우편번호 입력 필요")
        String post,
        @NotBlank(message = "공연장주소 입력 필요")
        String basicAddr,
        @NotBlank(message = "상세주소 입력")
        String detailAddr,
        @NotBlank(message = "공연장 상태 입력")
        String stat,
        @Min(value = 50, message = "최소한 50이상 입력")
        int seatScale,
        String structure,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
