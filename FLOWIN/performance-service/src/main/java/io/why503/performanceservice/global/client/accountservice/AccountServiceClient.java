package io.why503.performanceservice.global.client.accountservice;

import io.why503.performanceservice.global.client.accountservice.dto.CompanyInfoResponse;
import io.why503.performanceservice.global.client.accountservice.dto.UserRoleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account-service")
public interface AccountServiceClient {


    //메서드 파라미터 이름이 url 경로 변수와 동일하다면 어노테이션 안의 이름 생략 가능
    /**
     * 로그인 사용자 기준 회사 정보 조회
     * - COMPANY 권한 검증은 account-service 책임
     * - 성공 시 companySq 반환
     */
    @GetMapping("/accounts/company/{sq}")
    CompanyInfoResponse getMyCompanyInfo(@PathVariable Long sq);

    /**
     * account-service의 상세 조회 API를 호출
     */
    @GetMapping("/accounts/sq/{sq}")
    UserRoleResponse getUserRole(@PathVariable Long sq);
}