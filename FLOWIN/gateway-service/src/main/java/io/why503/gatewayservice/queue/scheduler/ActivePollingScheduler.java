package io.why503.gatewayservice.queue.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Active 상태 보정용 Scheduler (보험 장치)
 * 역할:
 * - EntryToken TTL 만료, 서버 재기동 등으로 인해
 *  active값이 실제 EntryToken 개수와 불일치 하는 경우를 주기적으로 보정
 * 
 * 설계 의도:
 * - 요청 기반 Lazy 보정을 기본으로 사용하되,
 *  요청이 전혀 없는 상황에서도 시스템 상태를 정합하기 위한 보조 수단으로 만들었음 
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActivePollingScheduler {

    private final StringRedisTemplate redisTemplate;

    private static final String ACTIVE_INDEX_KEY = "active:performance:index";

    // 보험용 Polling
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

    // ==== 내부 유틸 ====

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
