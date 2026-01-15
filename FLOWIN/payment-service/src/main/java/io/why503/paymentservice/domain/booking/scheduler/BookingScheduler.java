package io.why503.paymentservice.domain.booking.scheduler;

import io.why503.paymentservice.domain.booking.sv.BookingSv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingScheduler {

    private final BookingSv bookingSv;
    private static final long SCAN_INTERVAL_MS = 60 * 1000;

    /**
     * [자동 취소 스케줄러]
     * - 주기: 1분 (이전 작업 종료 후 1분 대기)
     * - 대상: 10분이 지나도 결제되지 않은(PENDING) 예매 건
     */
    @Scheduled(fixedDelay = SCAN_INTERVAL_MS)
    public void autoCancelExpiredBookings() {
        long startTime = System.currentTimeMillis();
        // 서비스 로직 실행
        int deletedCount = bookingSv.cancelExpiredBookings();
        long endTime = System.currentTimeMillis();
        // "처리가 발생했을 때만" 로그를 남김 (Silence is Golden)
        if (deletedCount > 0) {
            log.info("[Scheduler] 만료된 예매 {}건 자동 취소 완료 (소요시간: {}ms)",
                    deletedCount, (endTime - startTime));
        }
    }
}