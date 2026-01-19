package io.why503.accountservice.domain.companies.model.dto.vo;

import io.why503.accountservice.domain.companies.model.enums.CompanyBank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/*
Req -> Ett로 갈 때
사용은 mapper에서 사용
 */
public record CompanyVo(
        @NotNull CompanyBank companyBank,
        @NotBlank String accountNumber,
        @NotBlank String companyName,
        @NotBlank String ownerName,
        @NotBlank String companyPhone,
        @NotBlank String companyEmail,
        @NotBlank String companyBasicAddr,
        @NotBlank String companyDetailAddr,
        @NotBlank String companyPost
){}
