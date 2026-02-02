package io.why503.gatewayservice.entrytoken.service.impl;

import io.why503.gatewayservice.entrytoken.service.EntryTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

// EntryToken의 존재 여부 검증
@Service
@RequiredArgsConstructor
public class EntryTokenValidatorImpl implements EntryTokenValidator {

    private final StringRedisTemplate redisTemplate;

    @Override
    // 토큰 존재?
    public boolean isValid(String showId, String userId) {
        String key = tokenKey(showId, userId);

        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // 현재 예매 페이지에 접근 가능한 상태임을 나타내는 토큰
    private String tokenKey(String showId, String userId) {
        return "entry:token:" + showId + ":" + userId;
    }
}
