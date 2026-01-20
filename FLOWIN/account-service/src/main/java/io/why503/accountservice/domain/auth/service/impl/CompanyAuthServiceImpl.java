/**
 * Company Email Authentication Service
 * 사용 목적 :
 * - 회사 이메일로 인증 코드 발송
 * - 인증 코드 Redis 저장 및 유효시간 관리
 */
package io.why503.accountservice.domain.auth.service.impl;

import java.util.concurrent.TimeUnit;

import io.why503.accountservice.domain.auth.service.CompanyAuthService;
import io.why503.accountservice.util.AuthCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyAuthServiceImpl implements CompanyAuthService {

    private final JavaMailSender mailSender;            // 이메일 발송 처리 컴포넌트
    private final StringRedisTemplate redisTemplate;    // 인증 코드 Redis 저장/조회용 Template

    @Value("${custom.mail.from}")
    private String mailFrom;                            // 발신자 이메일 (yml 관리)

    @Value("${custom.mail.auth_code_ttl}")
    private long AUTH_CODE_TTL;      // 인증 코드 유효 시간 (초 단위, 5분)

    public void sendAuthCode(String email) {

        String authCode = AuthCodeGenerator.generate(); // 랜덤 인증 코드 생성

        // 인증 코드 Redis 저장 (TTL 기반 자동 만료)
        String key = buildKey(email);
        redisTemplate.opsForValue()
            .set(key, authCode, AUTH_CODE_TTL, TimeUnit.SECONDS);

        // 인증 코드 이메일 발송 메시지 구성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(email);
        message.setSubject("[Company Service] 이메일 인증 코드");
        message.setText(
            "## 사업자 등록증, 인감 증명서 등 서류 제출을 인증 코드로 생략 ##\n" +
            "인증 코드: " + authCode + "\n" +
            "인증 코드는 5분 후 만료됩니다."
        );

        mailSender.send(message);

        log.info("회사 이메일 인증 코드 발송 완료. email={}", email);
    }
    private String buildKey(String email) {
        return "company:email:auth:" + email;
    }

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
