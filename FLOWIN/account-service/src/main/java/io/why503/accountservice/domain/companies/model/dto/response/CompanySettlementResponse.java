package io.why503.accountservice.domain.companies.model.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompanySettlementResponse(
        @NotNull Long companySq,
        @NotBlank String bank,
        @NotBlank String accountNumber,
        @NotBlank String ownerName
) {
}