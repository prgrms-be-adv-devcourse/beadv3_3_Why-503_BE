package io.why503.performanceservice.global.client.accountservice;

import io.why503.performanceservice.global.client.accountservice.dto.CompanyInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "account-service")
public interface AccountServiceClient {

    /**
     * 로그인 사용자 기준 회사 정보 조회
     * - COMPANY 권한 검증은 account-service 책임
     * - 성공 시 companySq 반환
     */
    @GetMapping("/internal/users/me/company-info")
    CompanyInfoResponse getMyCompanyInfo(
            @RequestHeader("Authorization") String authorization
    );
}
