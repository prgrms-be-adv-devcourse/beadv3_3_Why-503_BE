package io.why503.accountservice.domain.accounts.model.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/*
sq, name, Role만 반환하는 컴팩트한 응답
 */
public record UserRoleResponse(
        @NotNull Long userSq,
        @NotBlank String userName,
        @NotNull int userRole){
}
