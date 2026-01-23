package io.why503.accountservice.domain.accounts.model.dto.requests;

public record PointUseRequest(
        Long amount // 사용할(차감할) 포인트 금액
) {
}
