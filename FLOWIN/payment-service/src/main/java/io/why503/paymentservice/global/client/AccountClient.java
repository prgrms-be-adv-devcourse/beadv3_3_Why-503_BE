package io.why503.paymentservice.global.client;

import io.why503.paymentservice.global.client.dto.response.AccountResponse;
import io.why503.paymentservice.global.client.dto.request.PointUseRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 계정 서비스와 통신하여 사용자 포인트 정보 조회 및 증감을 처리하는 클라이언트
 */
@FeignClient(name = "account-service", url = "http://account-service:8100")
public interface AccountClient {

    // 사용자 식별자를 통한 계정 포인트 정보 조회
    @GetMapping("/accounts/point/{sq}")
    AccountResponse getAccount(@PathVariable("sq") Long userSq);

    // 결제 취소 또는 적립 시 사용자의 포인트 잔액 증가
    @PostMapping("/accounts/point/increase/{sq}")
    void increasePoint(
            @PathVariable("sq") Long userSq,
            @RequestBody PointUseRequest request);

    // 결제 승인 시 사용자의 포인트 잔액 차감
    @PostMapping("/accounts/point/decrease/{sq}")
    void decreasePoint(
            @PathVariable("sq") Long userSq,
            @RequestBody PointUseRequest request);
}