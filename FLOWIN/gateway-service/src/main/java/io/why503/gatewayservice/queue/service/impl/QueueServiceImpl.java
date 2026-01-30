package io.why503.gatewayservice.queue.service.impl;

import io.why503.gatewayservice.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {

    private final StringRedisTemplate redisTemplate;

    // 동시에 예매 페이지에 들어갈 수 있는 최대 인원
    private final Integer maxActive; // QueueConfig 에서 주입

    private static final String ACTIVE_INDEX_KEY = "active:performance:index";

    // 바로 입장 가능한지 판단
    @Override
    public boolean canEnter(String showId, String userId) {

        // Lazy 보정 (요청 시점)
        adjustActiveIfNeeded(showId);

        // 현재 active 수 조회
        String activeKey = activeKey(showId);
        String activeStr = redisTemplate.opsForValue().get(activeKey);
        int active = activeStr == null ? 0 : Integer.parseInt(activeStr);

        // 입장 가능하면 active 증가
        if (active < maxActive) {

            Long increased = redisTemplate
                    .opsForValue()
                    .increment(activeKey);

            // active가 0 -> 1 이 되는 순간 index에 showId 등록
            if (increased != null && increased == 1L) {
                redisTemplate.opsForSet()
                        .add(ACTIVE_INDEX_KEY, showId);
            }

            // 대기열에 있던 사용자라면 ZSET 제거
            // active가 비는 순간 그 직후 들어오는 요청은 대기열 맨 앞 유저가 보낸 요청
            redisTemplate.opsForZSet().remove(queueKey(showId), userId); 
    
            return true;
        }
        return false;
    }

    // 대기열 진입
    @Override
    public void enqueue(String showId, String userId) {

        if (isAlreadyQueued(showId, userId)) {
            return;
        }

        String queueKey = queueKey(showId);
        String seqKey = seqKey(showId);

        // 절대로 겹치지 않는 순번 생성
        Long seq = redisTemplate.opsForValue()
                .increment(seqKey);

        // ZSET에 대기열 추가
        redisTemplate.opsForZSet()
                .add(queueKey, userId, seq.doubleValue());
    }

    // 이미 대기열에 들어가 있는가??
    @Override
    public boolean isAlreadyQueued(String showId, String userId) {
        return redisTemplate.opsForZSet()
                .score(queueKey(showId), userId) != null;
    }

    @Override
    public Long getQueuePosition(String showId, String userId) {
        String key = queueKey(showId);

        Long rank = redisTemplate
                .opsForZSet()
                .rank(key,userId);

        return rank == null ? null : rank + 1;
    }

    @Override
    public Long getQueueSize(String showId) {
        return redisTemplate
                .opsForZSet()
                .zCard(queueKey(showId));
    }
        
    // 요청 시점 Lazy 보정 - entry token 실개수 기준으로 active 보정
    private void adjustActiveIfNeeded(String showId) {

        String activeKey = activeKey(showId);
        String activeStr = redisTemplate.opsForValue().get(activeKey);
        int active = activeStr == null ? 0 : Integer.parseInt(activeStr);

        if (active == 0) {
            return;
        }

        int realTokenCount = countEntryTokens(showId);

        // active가 실제보다 크면 보정
        if (active > realTokenCount) {
            redisTemplate.opsForValue()
                    .set(activeKey, String.valueOf(realTokenCount));

            // 완전히 종료된 경우 index에서도 제거
            if (realTokenCount == 0) {
                redisTemplate.opsForSet()
                        .remove(ACTIVE_INDEX_KEY, showId);
            }
        }
    }

    // 실제 EntryToken 개수 조회 (showId 단위로만 조회)
    private int countEntryTokens(String showId) {

        String pattern = "entry:token:" + showId + ":*";
        Set<String> tokens = redisTemplate.keys(pattern);

        return tokens == null ? 0 : tokens.size();
    }

    // ==== Redis Key 규칙 ====

    // 대기열(줄)
    private String queueKey(String showId) {
        return "queue:performance:" + showId;
    }

    // 번호표 발급기 (INCR로 절대 안겹치는 순번 생성)
    private String seqKey(String showId) {
        return "queue:seq:performance:" + showId;
    }

    // 현재 예매 페이지 안에 들어가 있는 사람 수 -> 즉 Entry Token을 가진 사람 수
    private String activeKey(String showId) {
        return "active:performance:" + showId;
    }

    
}