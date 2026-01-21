package io.why503.gatewayservice.queue.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * - active:performance:{showId}      -> 현재 active 수
 * - entry:token:{showId}:{userId}    -> 입장권
 * - active:performance:index         -> active 상태인 showId 목록
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActivePollingScheduler {

    private final StringRedisTemplate redisTemplate;

    private static final String ACTIVE_INDEX_KEY = "active:performance:index";

    /**
     * 보험용 Polling
     * - 운영에서는 2~5분 권장
     */
    @Scheduled(fixedDelay = 180_000)
    public void reconcileActiveByIndex() {

        // 현재 active 상태인 공연만 조회
        Set<String> showIds = redisTemplate.opsForSet()
                .members(ACTIVE_INDEX_KEY);

        if (showIds == null || showIds.isEmpty()) {
            return;
        }

        for (String showId : showIds) {

            String activeKey = activeKey(showId);

            int active = getActive(activeKey);
            int realTokenCount = countEntryTokens(showId);

            // Lazy가 놓친 경우만 보정
            if (active > realTokenCount) {

                if (realTokenCount == 0) {
                    // 완전 종료 → index에서도 제거
                    redisTemplate.opsForSet()
                            .remove(ACTIVE_INDEX_KEY, showId);
                }

                redisTemplate.opsForValue()
                        .set(activeKey, String.valueOf(realTokenCount));

                log.info(
                        "[POLLING-INDEX] active corrected | showId={} | {} -> {}",
                        showId, active, realTokenCount
                );
            }
        }
    }

    /* =======================
       내부 유틸
       ======================= */

    private int getActive(String activeKey) {
        String value = redisTemplate.opsForValue().get(activeKey);
        return value == null ? 0 : Integer.parseInt(value);
    }

    private int countEntryTokens(String showId) {
        String pattern = "entry:token:" + showId + ":*";
        Set<String> tokens = redisTemplate.keys(pattern);
        return tokens == null ? 0 : tokens.size();
    }

    private String activeKey(String showId) {
        return "active:performance:" + showId;
    }
}
