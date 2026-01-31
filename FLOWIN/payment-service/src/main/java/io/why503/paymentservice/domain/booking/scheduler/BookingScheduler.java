package io.why503.paymentservice.domain.booking.scheduler;

import io.why503.paymentservice.domain.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingScheduler {

    private final BookingService bookingService;

    @Value("${scheduler.booking.expiration-minutes:10}") // 기본값 10분 설정 (안전장치)
    private int expirationMinutes;

    /**
     * 만료된 예매 자동 취소
     * - @Scheduled: 정해진 주기마다 실행
     * - (주의) 서버가 여러 대일 경우 ShedLock 등을 적용하여 중복 실행을 막아야 합니다.
     */
    @Scheduled(cron = "${scheduler.booking.cron}")
    public void autoCancelExpiredBookings() {
        long startTime = System.currentTimeMillis();

        try {
            log.info("[Scheduler] 만료 예매 정리 시작 (기준: {}분 전 생성)", expirationMinutes);

            // 서비스 로직 실행
            int deletedCount = bookingService.cancelExpiredBookings(expirationMinutes);

            long duration = System.currentTimeMillis() - startTime;

            if (deletedCount > 0) {
                log.info("[Scheduler] 정리 완료: 총 {}건 취소됨 (소요: {}ms)", deletedCount, duration);
            } else {
                log.debug("[Scheduler] 정리 대상 없음 (소요: {}ms)", duration);
            }

        } catch (Exception e) {
            // 스케줄러가 죽지 않도록 예외를 잡아서 로깅
            log.error("[Scheduler] 예매 정리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}