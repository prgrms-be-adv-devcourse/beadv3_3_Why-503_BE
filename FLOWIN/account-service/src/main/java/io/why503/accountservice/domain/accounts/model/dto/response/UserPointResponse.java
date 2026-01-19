package io.why503.accountservice.domain.accounts.model.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserPointResponse(
        @NotBlank String userName,
        @NotNull Long userPoint
) {
}
