package io.why503.paymentservice.domain.booking.scheduler;

import io.why503.paymentservice.domain.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 예매 자동 취소 스케줄러
 * - 결제 대기 상태로 유효 시간이 지난 예매 건을 주기적으로 정리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingScheduler {

    private final BookingService bookingService;

    // 만료 기준 시간 (yml에서 주입)
    @Value("${scheduler.booking.expiration-minutes}")
    private int expirationMinutes;

    // 만료된 예매 자동 취소
    // - 주기: 1분
    // - 대상: 10분이 지나도 결제되지 않은(PENDING) 예매 건
    @Scheduled(cron = "${scheduler.booking.cron}")
    public void autoCancelExpiredBookings() {
        long startTime = System.currentTimeMillis();

        // 서비스 로직 실행 (만료 시간 파라미터 전달)
        int deletedCount = bookingService.cancelExpiredBookings(expirationMinutes);

        // 처리된 건이 있을 때만 로그 기록
        if (deletedCount > 0) {
            long duration = System.currentTimeMillis() - startTime;
            log.info("[Scheduler] 만료된 예매 {}건 자동 취소 완료 (기준: {}분, 소요: {}ms)",
                    deletedCount, expirationMinutes, duration);
        }
    }
}