package io.why503.accountbase.model.response;

import jakarta.validation.constraints.NotNull;

//회사 시퀸스 반환에 사용
public record UserCompanyResponse(
        @NotNull Long companySq
) {
}
