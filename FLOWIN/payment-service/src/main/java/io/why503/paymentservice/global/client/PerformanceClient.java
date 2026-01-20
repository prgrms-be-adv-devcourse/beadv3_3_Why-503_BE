package io.why503.paymentservice.global.client;

import io.why503.paymentservice.global.client.dto.RoundSeatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "performance-service")
public interface PerformanceClient {

    /**
     * [좌석 선점 요청]
     */
    @PostMapping("/round-seats/reserve") //회차 좌석을 선점 상태로 변경
    List<RoundSeatResponse> reserveRoundSeats(@RequestBody List<Long> roundSeatIds);

    /**
     * [좌석 선점 취소 요청]
     */
    @PostMapping("/round-seats/cancel") //회차 좌석을 예매 가능 상태로 변경
    void cancelRoundSeats(@RequestBody List<Long> roundSeatIds);

    /**
     * [좌석 판매 완료 요청]
     */
    @PostMapping("/round-seats/confirm") //회차 좌석을 판매 완료 상태로 변경
    void confirmRoundSeats(@RequestBody List<Long> roundSeatIds);
}