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
@FeignClient(name = "performance-service", url = "http://localhost:8200")
public interface PerformanceClient {

    // 특정 사용자를 위한 좌석 점유권 확보 요청
    @PostMapping("/round-seats/reserve")
    List<RoundSeatResponse> reserveRoundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody List<Long> roundSeatSqs
    );

    // 유효 시간 만료 또는 취소 요청에 따른 좌석 점유 해제
    @PostMapping("/round-seats/cancel")
    void cancelRoundSeats(@RequestBody List<Long> roundSeatSqs);

    // 실결제 완료 후 점유된 좌석의 영구 상태 전환 요청
    @PostMapping("/round-seats/confirm")
    void confirmRoundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody List<Long> roundSeatSqs
    );
}