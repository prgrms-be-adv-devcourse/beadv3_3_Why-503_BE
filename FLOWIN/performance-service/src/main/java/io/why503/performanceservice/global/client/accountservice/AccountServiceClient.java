package io.why503.performanceservice.global.client.accountservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import io.why503.performanceservice.global.client.accountservice.dto.CompanyInfoResponse;

@FeignClient(name = "account-service")
public interface AccountServiceClient {

    @GetMapping("/internal/users/me/company-info")
    CompanyInfoResponse getMyCompanyInfo(
            @RequestHeader("Authorization") String authorization
    );
}
