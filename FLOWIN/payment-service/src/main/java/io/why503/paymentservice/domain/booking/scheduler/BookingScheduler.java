package io.why503.paymentservice.domain.booking.scheduler;

import io.why503.paymentservice.domain.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private static final long SCAN_INTERVAL_MS = 60 * 1000; // 1분

    /**
     * 만료된 예매 자동 취소
     * - 주기: 1분
     * - 대상: 10분이 지나도 결제되지 않은(PENDING) 예매 건
     */
    @Scheduled(fixedDelay = SCAN_INTERVAL_MS)
    public void autoCancelExpiredBookings() {
        long startTime = System.currentTimeMillis();

        // 서비스 로직 실행
        int deletedCount = bookingService.cancelExpiredBookings();

        // 처리된 건이 있을 때만 로그 기록
        if (deletedCount > 0) {
            long duration = System.currentTimeMillis() - startTime;
            log.info("[Scheduler] 만료된 예매 {}건 자동 취소 완료 (소요시간: {}ms)", deletedCount, duration);
        }
    }
}