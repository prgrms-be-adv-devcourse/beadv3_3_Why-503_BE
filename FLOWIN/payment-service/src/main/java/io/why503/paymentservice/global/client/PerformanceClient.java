package io.why503.paymentservice.global.client;

import io.why503.paymentservice.global.client.dto.RoundSeatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Performance Service 통신 클라이언트
 * - 좌석 선점, 취소, 판매 확정 등의 상태 변경 요청을 수행합니다.
 */
@FeignClient(name = "performance-service")
public interface PerformanceClient {

    /**
     * 좌석 선점 요청
     * - 예매 시작 시 좌석을 '선점' 상태로 변경하여 중복 예매를 방지합니다.
     */
    @PostMapping("/round-seats/reserve")
    List<RoundSeatResponse> reserveRoundSeats(@RequestBody List<Long> roundSeatIds);

    /**
     * 좌석 선점 취소 요청
     * - 예매 취소 또는 실패 시 좌석을 다시 '예매 가능' 상태로 되돌립니다.
     */
    @PostMapping("/round-seats/cancel")
    void cancelRoundSeats(@RequestBody List<Long> roundSeatIds);

    /**
     * 좌석 판매 완료 요청
     * - 결제 승인 완료 시 좌석을 '판매 완료' 상태로 확정합니다.
     */
    @PostMapping("/round-seats/confirm")
    void confirmRoundSeats(@RequestBody List<Long> roundSeatIds);
}