package io.why503.companyservice.Sv;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyEmailVerifyService {
    private final StringRedisTemplate redisTemplate;

    public boolean verify(String email, String inputCode) {

        String key = "company:email:auth:" + email;
        String savedCode = redisTemplate.opsForValue().get(key);

        if (savedCode == null) {
            return false; // 만료 or 없음
        }

        if (!savedCode.equals(inputCode)) {
            return false; // 불일치
        }

        // 인증 성공
        redisTemplate.delete(key);
        return true;
    }
}
