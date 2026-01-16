package io.why503.paymentservice.global.client;

import io.why503.paymentservice.global.client.dto.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


//추후 테스트 진행
// name: 유레카에 등록된 회원 서비스 이름
@FeignClient(name = "account-service")
public interface AccountClient {
    // 회원 상세 조회 API 주소
    @GetMapping("/account/{sq}")
    AccountResponse getAccount(@PathVariable("sq") Long userSq);
}