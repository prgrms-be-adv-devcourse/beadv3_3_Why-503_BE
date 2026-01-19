package io.why503.accountservice.domain.accounts.model.dto.response;

import jakarta.validation.constraints.NotNull;

public record UserCompanyResponse(
        @NotNull Long companySq
) {
}
