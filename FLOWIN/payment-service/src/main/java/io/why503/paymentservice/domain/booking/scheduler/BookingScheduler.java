package io.why503.paymentservice.domain.booking.scheduler;

import io.why503.paymentservice.domain.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 정해진 주기마다 미결제된 예매 건을 정리하는 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingScheduler {

    private final BookingService bookingService;

    @Value("${scheduler.booking.expiration-minutes:10}")
    private int expirationMinutes;

    // 일정 시간 동안 결제가 진행되지 않은 예매 건을 자동 취소
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