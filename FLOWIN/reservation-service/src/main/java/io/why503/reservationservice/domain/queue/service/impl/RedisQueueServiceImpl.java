package io.why503.reservationservice.domain.queue.service.impl;

import org.springframework.stereotype.Service;

import io.why503.reservationservice.domain.queue.model.QueueResult;
import io.why503.reservationservice.domain.queue.service.QueueService;

import org.springframework.data.redis.core.StringRedisTemplate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisQueueServiceImpl implements QueueService {
    private final StringRedisTemplate redisTemplate;

    // 고정 동시 진입 수 (동적 조절 로직을 만들면 좋을거 같은데 일단 고정 값으로 진행하였음)
    private static final int MAX_ACTIVE = 1;

    // Queue 키
    private static String queueKey(Long roundSq) {
        return "queue:round:" + roundSq;
    }
    // Entry 키
    private static String activeKey(Long roundSq) {
        return "active:round:" + roundSq;
    }

    @Override
    public QueueResult tryEnter(Long roundSq, Long userSq) {

        // 1) active 수가 MAX_ACTIVE를 초과했는가?
        Long activeCount = redisTemplate.opsForSet().size(activeKey(roundSq));
        if (activeCount != null && activeCount < MAX_ACTIVE) {
            // 1-1) 이랏 샤이 마세
            redisTemplate.opsForSet().add(activeKey(roundSq), userSq.toString());
            return QueueResult.enter();
        }

        // 2) 대기열(ZSET)에 추가
        String qKey = queueKey(roundSq);
        String member = userSq.toString();

        // 중복 등록 방지 ::: 이미 있으면 score 갱신 안함 (분신술 금지)
        Boolean added = redisTemplate.opsForZSet()
                .addIfAbsent(qKey, member, System.currentTimeMillis());
        
        // 3) 대기 순번 계산 로직 (0 아니라 1부터 세기 시작합니다요)
        Long rank = redisTemplate.opsForZSet().rank(qKey,member);
        Long position = (rank != null) ? rank + 1 : null;

        return QueueResult.waiting(position);
    }
}
