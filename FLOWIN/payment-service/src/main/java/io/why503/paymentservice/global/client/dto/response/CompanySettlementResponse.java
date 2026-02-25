package io.why503.paymentservice.global.client.dto.response;

public record CompanySettlementResponse(
        Long companySq,
        String bank,
        String accountNumber,
        String ownerName
) {
}