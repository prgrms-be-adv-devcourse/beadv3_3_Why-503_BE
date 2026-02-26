package io.why503.accountservice.domain.accounts.model.dto.response;

import jakarta.validation.constraints.NotNull;

//유저 포인트 반환에 사용
public record UserPointResponse(
        @NotNull Long userPoint
) {
}
