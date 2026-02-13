package io.why503.reservationservice.domain.queue.service.impl;

import org.springframework.stereotype.Service;

import io.why503.reservationservice.domain.entry.service.EntryTokenService;
import io.why503.reservationservice.domain.queue.model.QueueResult;
import io.why503.reservationservice.domain.queue.model.QueueStatusResponse;
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
    // Redis Pub/Sub 채널명 ( active 자리가 비었을때의 승격 트리거로 사용 )
    private static final String PROMOTE_CHANNEL = "queue:promote";

    // Queue 키
    private static String queueKey(Long roundSq) {
        return "queue:round:" + roundSq;
    }
    // Entry 키
    private static String activeKey(Long roundSq) {
        return "active:round:" + roundSq;
    }

    // 대기열 진입 시도
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

        // 0-2) 이미 queue(대기열)에 있는지 확인
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

        // 2) 자리가 없으면 대기열(ZSET)에 등록
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

        // Redis pub/sub 발행(승격 이벤트)
        redisPubTemplate.convertAndSend(
            PROMOTE_CHANNEL, 
            roundSq.toString()
        );
    }

    // 다음 대기자 승격 처리
    @Override
    public void promoteNext(Long roundSq) {

        String aKey = activeKey(roundSq);
        String qKey = queueKey(roundSq);

        Long active_count = redisTemplate.opsForSet().size(aKey);
        long current_active = (active_count != null) ? active_count : 0L;

        if (current_active >= MAX_ACTIVE) {
            return;
        }

        // 대기열에서 가장 앞 순번 유저 추출
        var tuple = redisTemplate.opsForZSet().popMin(qKey);

        if (tuple == null) {
            return;
        }

        String next_user = tuple.getValue();

        // 꺼낸 앞 순번 유저를 active SET에 등록시키기
        redisTemplate.opsForSet().add(aKey, next_user);

        // entry token 발급
        Long userSq = Long.parseLong(next_user);
        entryTokenService.issue(userSq, roundSq);
    }

    @Override
    public QueueStatusResponse getStatus(Long roundSq, Long userSq) {
        String aKey = activeKey(roundSq);
        String qKey = queueKey(roundSq);
        String member = userSq.toString();

        // 1. active 자리가 비어있으면 자동 승격 시도
        Long activeSize = redisTemplate.opsForSet().size(aKey);
        if (activeSize == null || activeSize < MAX_ACTIVE) {
            promoteNext(roundSq);
        }

        Boolean isActive = redisTemplate.opsForSet().isMember(aKey, member);
        if (Boolean.TRUE.equals(isActive)) {
            return new QueueStatusResponse(
                    "ENTER",
                    null,
                    null,
                    null,
                    null
            );
        }

        Long rank = redisTemplate.opsForZSet().rank(qKey, member);

        if (rank == null) {
            return new QueueStatusResponse(
                    "WAITING",
                    null,
                    0L,
                    0L,
                    300L
            );
        }

        Long totalWaiting = redisTemplate.opsForZSet().size(qKey);

        return new QueueStatusResponse(
                "WAITING",
                rank + 1,
                totalWaiting,
                100L,
                300L
        );
    }

}
