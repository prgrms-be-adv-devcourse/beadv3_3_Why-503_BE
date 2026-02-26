package io.why503.performanceservice.global.client.accountservice;

import io.why503.performanceservice.global.client.accountservice.dto.CompanyInfoResponse;
import io.why503.performanceservice.global.client.accountservice.dto.UserRoleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account-service")//, url = "http://account-service:8100")
public interface AccountServiceClient {

    // 로그인 사용자 기준 회사 정보 조회
    @GetMapping("/accounts/company/{sq}")
    CompanyInfoResponse getMyCompanyInfo(@PathVariable("sq") Long sq);

    // account-service의 상세 조회 API를 호출
    @GetMapping("/accounts/sq/{sq}")
    UserRoleResponse getUserRole(@PathVariable("sq") Long sq);
}