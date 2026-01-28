package io.why503.gatewayservice.entrytoken.service.impl;

import io.why503.gatewayservice.entrytoken.service.EntryTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * EntryTokenValidatorImpl

 * - EntryToken의 유효성(존재 여부) 검증
 *
 * - 검증만 담당 (발급/회수 X)
 */
@Service
@RequiredArgsConstructor
public class EntryTokenValidatorImpl implements EntryTokenValidator {

    private final StringRedisTemplate redisTemplate;

    

    @Override
    public boolean isValid(String showId, String userId) {
        String key = tokenKey(showId, userId);
        System.out.println("[ENTRY TOKEN] key = " + key);
        System.out.println("[ENTRY TOKEN] exists = " + redisTemplate.hasKey(key));
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }


    private String tokenKey(String showId, String userId) {
        return "entry:token:" + showId + ":" + userId;
    }
    
    
}
