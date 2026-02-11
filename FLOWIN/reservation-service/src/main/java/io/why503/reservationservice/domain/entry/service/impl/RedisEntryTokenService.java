package io.why503.reservationservice.domain.entry.service.impl;

import io.why503.reservationservice.domain.entry.service.EntryTokenService;

import java.time.Duration;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisEntryTokenService implements EntryTokenService {

    private final StringRedisTemplate redisTemplate;

    // 고정 TTL (메모사항 ::: 나중에 설정값으로 분리계획)
    private static final Duration TTL = Duration.ofMinutes(10);

    private static String key(Long roundSq, String token) {
        return "entry:token:" + roundSq + ":" + token;
    }

    @Override
    public String issue(Long userSq, Long roundSq) {

        String token = UUID.randomUUID().toString();
        String redisKey = key(roundSq, token);

        redisTemplate.opsForValue()
                .set(redisKey, userSq.toString(), TTL);

        return token;
    }
}
