package io.why503.theaterservice.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record hallRequest(
        @NotBlank(message = "이름 입력 필요")
        String name,
        @NotBlank(message = "우편번호 입력 필요")
        String post,
        @NotBlank(message = "공연장주소 입력 필요")
        String basic_addr,
        @NotBlank(message = "상세주소 입력")
        String detail_addr,
        @NotBlank(message = "공연장 상태 입력")
        int stat,
        @Min(value = 50, message = "최소한 50이상 입력")
        int seat_scale,
        Long id,
        String structure,
        int latitude,
        int longitude
) {
}
