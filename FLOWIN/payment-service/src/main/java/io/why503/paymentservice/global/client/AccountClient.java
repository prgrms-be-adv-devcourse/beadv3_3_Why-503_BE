package io.why503.paymentservice.global.client;

import io.why503.paymentservice.global.client.dto.AccountResponse;
import io.why503.paymentservice.global.client.dto.PointUseRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    /**
     * 포인트 사용 (차감) 요청
     * - 예매 확정 시 호출하여 실제 포인트를 차감합니다.
     */
    @PostMapping("/account/point/use")
    void usePoint(@RequestBody PointUseRequest request);

    /**
     * 포인트 사용 (환불) 요청
     * - 예매 확정 시 호출하여 실제 포인트를 차감합니다.
     */
    @PostMapping("/account/point/refund")
    void refundPoint(@RequestBody PointUseRequest request);
}