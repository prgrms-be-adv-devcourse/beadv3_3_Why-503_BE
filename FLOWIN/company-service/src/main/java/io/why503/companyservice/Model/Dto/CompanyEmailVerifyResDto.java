package io.why503.companyservice.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompanyEmailVerifyResDto {
    private boolean verified;
    private String message;
}
