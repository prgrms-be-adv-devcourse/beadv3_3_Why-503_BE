package io.why503.accountservice.domain.accounts.model.dto.response;

import io.why503.accountservice.domain.accounts.model.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/*
sq, name, Role만 반환하는 컴팩트한 응답
 */
public record UserRoleResponse(
        @NotNull Long userSq,
        @NotBlank String userName,
        @NotNull UserRole userRole){
}
