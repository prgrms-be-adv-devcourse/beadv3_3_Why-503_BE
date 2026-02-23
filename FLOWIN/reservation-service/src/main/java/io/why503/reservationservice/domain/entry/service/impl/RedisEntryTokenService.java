package io.why503.reservationservice.domain.entry.service.impl;

import io.why503.reservationservice.domain.entry.service.EntryTokenService;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.stereotype.Service;

// Active로 전환된 유저에게 entry token 발급
@Service
@RequiredArgsConstructor
public class RedisEntryTokenService implements EntryTokenService {

    private final StringRedisTemplate redisTemplate;

    // 고정 TTL (메모사항 ::: 나중에 설정값으로 분리계획)
    private static final Duration TTL = Duration.ofMinutes(100);

    // userSq 넣은 이유 : TTL 만료 시 expired 이벤트에서 user를 식별할 수 있기에 넣었음
    private static String key(Long roundSq, Long userSq) {
        return "entry:round:" + roundSq + ":user:" + userSq;
    }

    // active key (RedisQueueServiceImpl 과 동일 규칙)
    private static String activeKey(Long roundSq) {
        return "active:round:" + roundSq;
    }

    // 토큰 발급
    @Override
    public String issue(Long userSq, Long roundSq) {

        String token = UUID.randomUUID().toString(); // UUID기반 토큰
        String redisKey = key(roundSq, userSq);

        redisTemplate.opsForValue()
                .set(redisKey, token, TTL);

        return token;
    }

    // 토큰 회수
    @Override
    public void revokeByUserSq(Long userSq) {

        if (userSq == null || userSq <= 0) {
            return;
        }

        // entry:round:*:user:{userSq} 패턴으로 scan
        String pattern = "entry:round:*:user:" + userSq;

        Set<String> keys = scanKeys(pattern);

        if (keys.isEmpty()) {
            return;
        }

        // 1) 키가 1개면 그대로 삭제
        if (keys.size() == 1) {
            String onlyKey = keys.iterator().next();
            redisTemplate.delete(onlyKey);
            return;
        }

        // 2) 키가 여러 개면 active set에 실제로 들어있는 roundSq를 우선 선택해서 삭제
        for (String k : keys) {
            Long roundSq = parseRoundSqFromEntryKey(k);
            if (roundSq == null) continue;

            Boolean isActive = redisTemplate.opsForSet()
                    .isMember(activeKey(roundSq), String.valueOf(userSq));

            if (Boolean.TRUE.equals(isActive)) {
                redisTemplate.delete(k);
                return;
            }
        }

        // 3) 그래도 특정 못하면 안전하게 삭제하지 않는다 (오삭제 방지)
        // 필요하면 여기서 로그만 남기거나, 정책적으로 1개를 선택해 삭제하도록 변경 가능
    }

    // SCAN 기반으로 키를 모은다 (KEYS 사용 금지)
    private Set<String> scanKeys(String pattern) {

        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(200)
                .build();

        return redisTemplate.execute((RedisCallback<Set<String>>) (RedisConnection connection) -> {

            Set<String> results = new HashSet<>();

            try (var cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    byte[] raw = cursor.next();
                    results.add(new String(raw, StandardCharsets.UTF_8));
                }
            }

            return results;
        });
    }

    // entry:round:{roundSq}:user:{userSq} 에서 roundSq 파싱
    private Long parseRoundSqFromEntryKey(String key) {
        try {
            String[] parts = key.split(":");
            // [entry, round, {roundSq}, user, {userSq}]
            if (parts.length < 5) return null;
            return Long.parseLong(parts[2]);
        } catch (Exception e) {
            return null;
        }
    }
}