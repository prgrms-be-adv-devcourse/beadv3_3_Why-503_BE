package io.why503.paymentservice.global.client;

import io.why503.paymentservice.global.client.dto.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Account Service 통신 클라이언트
 * - 회원 정보 조회 등을 수행합니다.
 */
@FeignClient(name = "account-service")
public interface AccountClient {

    /**
     * 회원 상세 조회
     */
    @GetMapping("/account/sq/{sq}")
    AccountResponse getAccount(@PathVariable("sq") Long userSq);
}