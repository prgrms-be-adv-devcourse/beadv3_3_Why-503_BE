package io.why503.gatewayservice.entrytoken.service.impl;

import io.why503.gatewayservice.entrytoken.service.EntryTokenIssuer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * - EntryToken 발급 및 회수 서비스
 * - Entry는 active 감소만 담당 / 
 *   ((Queue는 증가만 / Lazy는 보정만 담당하게 설계)) 
 */

@Service
@RequiredArgsConstructor
public class EntryTokenIssuerImpl implements EntryTokenIssuer {
    private final StringRedisTemplate redisTemplate;

    // Active 공연 목록 관리용 인덱스 키 / active값이 0이 되면 해당 showId 제거 됨
    private static final String ACTIVE_INDEX_KEY = "active:performance:index";
    // Entry 토큰 유효 시간 장시간 무응답 및 이탈 시 사용자 토큰 자동 회수되게끔 설계
    private static final Duration ENTRY_TOKEN_TTL = Duration.ofMinutes(5);

    // EntryToken 발급
    @Override
    public void issue(String showId, String userId) {
        String tokenKey = tokenKey(showId, userId);

        // 이미 토큰이 있으면 재발급하지 않음
        Boolean exists = redisTemplate.hasKey(tokenKey);
        if (Boolean.TRUE.equals(exists)) {
            return;
        }

        // EntryToken 발급 + TTL 설정
        redisTemplate.opsForValue()
                .set(tokenKey, "ACTIVE", ENTRY_TOKEN_TTL);
    }

    // EntryToken 회수 ( 정상 종료 / 예매 완료 / 이탈 )
    @Override
    public void revoke(String showId, String userId) {
        String tokenKey = tokenKey(showId, userId);
        String activeKey = activeKey(showId);

        // 토큰 없음? -> 아무것도 하지 않음 ( 중복 감소 방지 )
        Boolean existed = redisTemplate.hasKey(tokenKey);
        if (!Boolean.TRUE.equals(existed)) {
            return;
        }

        // EntryToken 삭제
        redisTemplate.delete(tokenKey);

        // active 감소
        Long decreased = redisTemplate.opsForValue()
                .decrement(activeKey);

        if (decreased == null || decreased <= 0) {

            // active가 0 이하가 되면 index에서도 제거
            redisTemplate.opsForSet()
                    .remove(ACTIVE_INDEX_KEY, showId);

            // 음수 방지
            redisTemplate.opsForValue()
                    .set(activeKey, "0");
        }
    }

    // ==== Redis Key 규칙 ====

    // 입장 가능한 상태임을 나타내는 토큰
    private String tokenKey(String showId, String userId) {
        return "entry:token:" + showId + ":" + userId;
    }

    // 특정 공연 페이지에 활성 상태로 존재하는 사용자 수를 나타냄
    private String activeKey(String showId) {
        return "active:performance:" + showId;
    }
}