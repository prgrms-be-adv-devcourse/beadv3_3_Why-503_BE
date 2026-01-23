package io.why503.paymentservice.global.client;

import io.why503.paymentservice.global.client.dto.AccountResponse;
import io.why503.paymentservice.global.client.dto.PointUseRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Account Service 통신 클라이언트
 * - 수정사항: URL 경로를 /account -> /accounts 로 변경 (Controller와 일치시킴)
 */
@FeignClient(name = "account-service")
public interface AccountClient {

    /**
     * 회원 상세 조회
     */
    @GetMapping("/accounts/point/{sq}")
    AccountResponse getAccount(@PathVariable("sq") Long userSq);

    /**
     * 포인트 사용 증가
     */
    @PostMapping("/accounts/point/increase/{sq}")
    void increasePoint(
            @PathVariable("sq") Long userSq,
            @RequestBody PointUseRequest request);

    /**
     * 포인트 환불 감소
     */
    @PostMapping("/accounts/point/decrease/{sq}")
    void decreasePoint(
            @PathVariable("sq") Long userSq,
            @RequestBody PointUseRequest request);
}