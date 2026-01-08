package io.why503.companyservice.Model.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEmailVerifyReqDto {
    @NotBlank
    @Email
    private String companyEmail;

    @NotBlank
    private String authCode;
}
