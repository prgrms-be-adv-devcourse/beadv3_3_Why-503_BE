package io.why503.reservationservice.global.client;

import io.why503.reservationservice.global.client.dto.response.RoundSeatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * 공연 서비스와의 통신을 통해 좌석 점유 상태를 동기화하는 클라이언트
 */
@FeignClient(name = "performance-service")
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