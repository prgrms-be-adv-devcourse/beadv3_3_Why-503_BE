package io.why503.reservationservice.domain.queue.service.impl;

import org.springframework.stereotype.Service;

import io.why503.reservationservice.domain.entry.service.EntryTokenService;
import io.why503.reservationservice.domain.queue.model.QueueResult;
import io.why503.reservationservice.domain.queue.service.QueueService;
import org.springframework.data.redis.core.StringRedisTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@Service
@RequiredArgsConstructor
public class RedisQueueServiceImpl implements QueueService {
    private final StringRedisTemplate redisTemplate;
    private final RedisTemplate<String, String> redisPubTemplate;
    private final EntryTokenService entryTokenService;

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

    private static final String PROMOTE_CHANNEL = "queue:promote";

    @Override
    public QueueResult tryEnter(Long roundSq, Long userSq) {

        String aKey = activeKey(roundSq);
        String qKey = queueKey(roundSq);
        String member = userSq.toString();

        // 0-1) 이미 active인지 확인
        Boolean isActive = redisTemplate.opsForSet().isMember(aKey, member);
        if (Boolean.TRUE.equals(isActive)) {
            return QueueResult.enter();
        }

        // 0-2) 이미 queue에 있는지 확인
        Long existingRank = redisTemplate.opsForZSet().rank(qKey, member);
        if (existingRank != null) {
            return QueueResult.waiting(existingRank + 1);
        }
        // 1) [신규 진입 시도] active 수가 MAX_ACTIVE를 초과했는가?
        Long active_count = redisTemplate.opsForSet().size(aKey);
        long current_active = (active_count != null) ? active_count : 0L;

        if (current_active < MAX_ACTIVE) {
            redisTemplate.opsForSet().add(aKey, member);
            return QueueResult.enter();
        }

        // 2) 대기열 등록
        redisTemplate.opsForZSet()
                .add(qKey, member, System.currentTimeMillis());

        Long rank = redisTemplate.opsForZSet().rank(qKey, member);

        return QueueResult.waiting(rank + 1);
    }

    // 유저 나갔을때
    @Override
    public void leaveActive(Long roundSq, Long userSq) {

        String aKey = activeKey(roundSq);
        String member = userSq.toString();

        // 먼저 active key를 지워버리고
        redisTemplate.opsForSet().remove(aKey, member);

        // Redis pub/sub 발행
        redisPubTemplate.convertAndSend(PROMOTE_CHANNEL, roundSq.toString());
    }

    @Override
    public void promoteNext(Long roundSq) {

        String aKey = activeKey(roundSq);
        String qKey = queueKey(roundSq);

        Long active_count = redisTemplate.opsForSet().size(aKey);
        long current_active = (active_count != null) ? active_count : 0L;

        if (current_active >= MAX_ACTIVE) {
            return;
        }

        var tuple = redisTemplate.opsForZSet().popMin(qKey);

        if (tuple == null) {
            return;
        }

        String next_user = tuple.getValue();

        redisTemplate.opsForSet().add(aKey, next_user);

        // 챱츄 챱츄츄 챱츄 챱츄 챱챱츄
        // 여기서 entry token 발급
        // 또는 pub/sub 이벤트 발행 가능
    }
}
