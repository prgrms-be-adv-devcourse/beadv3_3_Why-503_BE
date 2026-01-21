package io.why503.performanceservice.global.client.accountservice;

import io.why503.performanceservice.global.client.accountservice.dto.CompanyInfoResponse;
import io.why503.performanceservice.global.client.accountservice.dto.UserRoleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "account-service")
public interface AccountServiceClient {

    /**
     * 로그인 사용자 기준 회사 정보 조회
     * - COMPANY 권한 검증은 account-service 책임
     * - 성공 시 companySq 반환
     */
    @GetMapping("/internal/account/company")
    CompanyInfoResponse getMyCompanyInfo(
            @RequestHeader("Authorization") String authorization
    );

    /**
     * account-service의 상세 조회 API를 호출
     */
    @GetMapping("/accounts/sq/{sq}")
    UserRoleResponse getUserRole(@PathVariable("sq") Long sq);
}