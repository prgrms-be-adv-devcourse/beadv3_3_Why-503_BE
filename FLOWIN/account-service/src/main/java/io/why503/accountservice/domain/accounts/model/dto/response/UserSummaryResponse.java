package io.why503.accountservice.domain.accounts.model.dto.response;

import io.why503.accountservice.domain.accounts.model.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

/*
sq, name, Role만 반환하는 컴팩트한 응답
 */
@Builder
public record UserSummaryResponse(
        @NotBlank Long sq,
        @NotBlank String name,
        @NotBlank UserRole role){
}
