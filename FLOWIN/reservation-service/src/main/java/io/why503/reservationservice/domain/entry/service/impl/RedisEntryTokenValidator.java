package io.why503.reservationservice.domain.entry.service.impl;

import io.why503.reservationservice.domain.entry.service.EntryTokenValidator;
import io.why503.reservationservice.domain.entry.util.EntryTokenExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisEntryTokenValidator implements EntryTokenValidator {

    private final StringRedisTemplate redisTemplate;

    private static String key(Long roundSq, Long userSq) {
        return "entry:round:" + roundSq + ":user:" + userSq;
    }

    @Override
    public void validate(Long userSq, Long roundSq) {
        if (userSq == null || roundSq == null) {
            throw EntryTokenExceptionFactory.entryTokenBadRequest("토큰 검증 정보가 누락되었습니다.");
        }

        Boolean exists = redisTemplate.hasKey(key(roundSq, userSq));
        if (exists == null || !exists) {
            throw EntryTokenExceptionFactory.entryTokenForbidden("예매 권한 토큰이 없거나 만료되었습니다.");
        }
    }
}