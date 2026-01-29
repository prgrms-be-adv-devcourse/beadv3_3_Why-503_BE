package io.why503.paymentservice.global.client;

import io.why503.paymentservice.global.client.dto.response.RoundSeatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "performance-service")
public interface PerformanceClient {

    // 좌석 선점 요청
    @PostMapping("/round-seats/reserve")
    List<RoundSeatResponse> reserveRoundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody List<Long> roundSeatSqs
    );

    // 좌석 선점 취소
    @PostMapping("/round-seats/cancel")
    void cancelRoundSeats(@RequestBody List<Long> roundSeatSqs);

    // 좌석 예매 확정
    @PostMapping("/round-seats/confirm")
    void confirmRoundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody List<Long> roundSeatSqs
    );
}