package io.why503.paymentservice.global.client;

import io.why503.paymentservice.global.client.dto.response.RoundSeatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 공연 서비스와 통신하여 좌석의 선점, 취소 및 예매 확정을 처리하는 클라이언트
 */
@FeignClient(name = "performance-service", url = "http://performance-account:8200")
public interface PerformanceClient {

    // 예매 시작 시 선택한 좌석들에 대한 임시 선점 요청
    @PostMapping("/round-seats/reserve")
    List<RoundSeatResponse> reserveRoundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody List<Long> roundSeatSqs
    );

    // 선점 기한 만료 또는 예매 취소 시 좌석 선점 해제 요청
    @PostMapping("/round-seats/cancel")
    void cancelRoundSeats(@RequestBody List<Long> roundSeatSqs);

    // 결제 완료 시 선점된 좌석을 최종 예매 확정 상태로 변경
    @PostMapping("/round-seats/confirm")
    void confirmRoundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody List<Long> roundSeatSqs
    );

    @PostMapping("/round-seats/details")
    List<RoundSeatResponse> findRoundSeats(@RequestBody List<Long> roundSeatSqs);
}