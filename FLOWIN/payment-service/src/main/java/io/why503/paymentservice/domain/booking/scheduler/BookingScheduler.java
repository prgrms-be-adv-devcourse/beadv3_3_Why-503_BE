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

    /**
     * 1분마다 실행
     * fixedDelay: 이전 작업이 '끝난 시점'부터 1분 뒤 실행 (중복 실행 방지)
     */
    @Scheduled(fixedDelay = 60000)
    public void autoCancelExpiredBookings() {
        log.info("[Scheduler] 만료된 예매 대기 건 자동 취소 시작...");

        long startTime = System.currentTimeMillis();

        // 서비스 로직 호출
        int cancelledCount = bookingSv.cancelExpiredBookings();

        long endTime = System.currentTimeMillis();

        if (cancelledCount > 0) {
            log.info("[Scheduler] 총 {}건의 미결제 예약을 취소했습니다. (소요시간: {}ms)",
                    cancelledCount, (endTime - startTime));
        } else {
            log.info("[Scheduler] 취소할 만료 예약이 없습니다.");
        }
    }
}