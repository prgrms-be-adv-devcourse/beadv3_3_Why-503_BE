package io.why503.reservationservice.global.client;

import io.why503.reservationservice.global.client.dto.response.RoundSeatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * 공연 서비스와 통신하여 실시간 좌석 가용 상태를 동기화하고 점유 권한을 제어하는 클라이언트
 */
@FeignClient(name = "performance-service")
public interface PerformanceClient {

    // 선택한 좌석에 대해 타 사용자의 접근을 제한하는 임시 점유 권한 획득
    @PostMapping("/round-seats/reserve")
    List<RoundSeatResponse> reserveRoundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody List<Long> roundSeatSqs
    );

    // 결제 미이행 또는 예매 철회 시 점유 중인 좌석 자원을 다시 판매 가능 상태로 방출
    @PostMapping("/round-seats/cancel")
    void cancelRoundSeats(@RequestBody List<Long> roundSeatSqs);

    // 결제 승인 완료에 따른 좌석의 최종 소유권 확정 및 판매 완료 처리
    @PostMapping("/round-seats/confirm")
    void confirmRoundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody List<Long> roundSeatSqs
    );

    // 다건의 좌석 식별자를 기반으로 공연 정보 및 가격 상세 데이터 추출
    @PostMapping("/round-seats/details")
    List<RoundSeatResponse> findRoundSeats(@RequestBody List<Long> roundSeatSqs);
}