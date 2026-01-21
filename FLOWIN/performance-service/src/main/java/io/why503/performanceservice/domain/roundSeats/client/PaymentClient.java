package io.why503.performanceservice.domain.roundSeats.client;


import io.why503.performanceservice.domain.roundSeats.model.dto.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    /**
     * [예매 생성 요청]
     * 내 서비스에서 선점(reserve) 성공 후, 결제 서비스의 BookingController를 호출합니다.
     */
    @PostMapping("/payments/booking")
    void createBooking(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody PaymentRequest request
    );
}