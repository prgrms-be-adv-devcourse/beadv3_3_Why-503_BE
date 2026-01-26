package io.why503.paymentservice.global.client;

import io.why503.paymentservice.global.client.dto.RoundSeatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "performance-service")
public interface PerformanceClient {

    /**
     * 좌석 선점 요청
     * - PerformanceService의 Controller가 X-USER-SQ를 사용하므로 헤더 추가 필요
     */
    @PostMapping("/round-seats/reserve")
    List<RoundSeatResponse> reserveRoundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody List<Long> roundSeatSqs
    );

    /**
     * 좌석 선점 취소
     * - Controller에서 헤더를 요구하지 않으므로 Body만 전송
     */
    @PostMapping("/round-seats/cancel")
    void cancelRoundSeats(@RequestBody List<Long> roundSeatSqs);

    /**
     * 좌석 예매 확정
     * - 예매 확정 시 소유자 검증을 위해 UserSq 전달 필요
     */
    @PostMapping("/round-seats/confirm")
    void confirmRoundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody List<Long> roundSeatSqs
    );
}