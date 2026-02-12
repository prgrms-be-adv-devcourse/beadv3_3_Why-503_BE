package io.why503.performanceservice.domain.roundSeat.scheduler;

import io.why503.performanceservice.domain.roundSeat.service.RoundSeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoundSeatScheduler {

    private final RoundSeatService roundSeatService;

    //1분마다 실행되는 스케줄러
    @Scheduled(fixedDelay = 60000)
    public void scheduleCleanup() {

        try {
            roundSeatService.cleanupExpiredReservations();
        } catch (Exception e) {
            // 혹시라도 서비스 호출 자체가 실패했을 때
            log.error("[Scheduler] 스케줄러 실행 중 예상치 못한 에러 발생", e);
        }
    }
}