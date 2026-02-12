package io.why503.reservationservice.domain.entry.service.impl;

import io.why503.reservationservice.domain.entry.service.EntryTokenService;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Active로 전환된 유저에게 entry token 발급
 */
@Service
@RequiredArgsConstructor
public class RedisEntryTokenService implements EntryTokenService {

    private final StringRedisTemplate redisTemplate;

    // 고정 TTL (메모사항 ::: 나중에 설정값으로 분리계획)
    private static final Duration TTL = Duration.ofMinutes(1);

    // userSq 넣은 이유 : TTL 만료 시 expired 이벤트에서 user를 식별할 수 있기에 넣었음
    private static String key(Long roundSq, Long userSq) {
        return "entry:round:" + roundSq + ":user:" + userSq;
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
    
}
