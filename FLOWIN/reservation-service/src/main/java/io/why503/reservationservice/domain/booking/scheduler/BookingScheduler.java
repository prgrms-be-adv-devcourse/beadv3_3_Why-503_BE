package io.why503.reservationservice.domain.booking.scheduler;

import io.why503.reservationservice.domain.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 정해진 주기마다 미결제된 예매 건을 정리하는 스케줄러
 * - 일정 시간 동안 결제가 완료되지 않은 점유 좌석을 회수
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingScheduler {

    private final BookingService bookingService;

    @Value("${scheduler.booking.expiration-minutes:10}")
    private int expirationMinutes;

    // 미결제 상태로 방치된 선점 좌석의 자동 해제 및 상태 변경
    @Scheduled(cron = "${scheduler.booking.cron}")
    public void autoCancelExpiredBookings() {
        long startTime = System.currentTimeMillis();

        try {
            log.info("[Scheduler] 만료 예매 정리 시작 (기준: {}분 전 생성)", expirationMinutes);

            int deletedCount = bookingService.cancelExpiredBookings(expirationMinutes);

            long duration = System.currentTimeMillis() - startTime;

            if (deletedCount > 0) {
                log.info("[Scheduler] 정리 완료: 총 {}건 취소됨 (소요: {}ms)", deletedCount, duration);
            } else {
                log.debug("[Scheduler] 정리 대상 없음");
            }
        } catch (Exception e) {
            log.error("[Scheduler] 만료 예매 정리 중 오류 발생: {}", e.getMessage());
        }
    }
}