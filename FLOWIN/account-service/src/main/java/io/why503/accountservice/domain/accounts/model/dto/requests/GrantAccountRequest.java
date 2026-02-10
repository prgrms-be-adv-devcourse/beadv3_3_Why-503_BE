package io.why503.accountservice.domain.accounts.model.dto.requests;

import io.why503.accountbase.model.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record GrantAccountRequest(
        @NotNull(message = "시퀀스 넘버")
        Long sq,
        @NotNull(message = "권한")
        UserRole role
) { }
