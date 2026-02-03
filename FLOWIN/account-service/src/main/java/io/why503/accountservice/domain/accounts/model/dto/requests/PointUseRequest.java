package io.why503.accountservice.domain.accounts.model.dto.requests;

import jakarta.validation.constraints.NotNull;

public record PointUseRequest(
        @NotNull(message = "포인트 금액")
        Long amount // 사용할(차감할) 포인트 금액
) { }
