/**
 * Redis 설정 클래스
 * 인증 코드(임시 데이터를 빠르게 저장/조회 하기 위해 사용)
 * 
 * 사용 목적 : 
 * - 회사 이메일 인증 코드 저장
 * - 인증 유효시간(TTL) 관리
 */

package io.why503.accountservice.domain.company.cfg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(
            RedisConnectionFactory connectionFactory // application.yml 기반 Redis 연결 정보를 주입받음
    ) {
        return new StringRedisTemplate(connectionFactory); // 문자열 기반 인증코드 관리 (String)
    }
}
