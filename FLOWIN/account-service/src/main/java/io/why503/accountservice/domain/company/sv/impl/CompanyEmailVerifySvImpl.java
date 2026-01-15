/**
 * Company Email Verification Service
 * 사용 목적 :
 * - Redis에 저장된 이메일 인증 코드 검증
 * - 인증 성공 시 인증 코드 즉시 제거
 */
package io.why503.accountservice.domain.company.sv.impl;

import io.why503.accountservice.domain.company.sv.CompanyEmailVerifySv;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyEmailVerifySvImpl implements CompanyEmailVerifySv {

    private final StringRedisTemplate redisTemplate; // 인증 코드 Redis 조회/삭제용 Template

    public boolean verify(String email, String inputCode) {

        String key = "company:email:auth:" + email; // 이메일 기준 Redis Key 생성
        String savedCode = redisTemplate.opsForValue().get(key); // Redis에 저장된 인증 코드 조회

        if (savedCode == null) {
            return false; // 인증 코드 만료 또는 존재하지 않음
        }

        if (!savedCode.equals(inputCode)) {
            return false; // 입력된 인증 코드 불일치
        }

        // 인증 성공 처리
        redisTemplate.delete(key); // 재사용 방지를 위해 인증 코드 즉시 삭제
        return true;
    }
}
