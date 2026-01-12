/**
 * Company Email Authentication Service
 *
 * 사용 목적 :
 * - 회사 이메일로 인증 코드 발송
 * - 인증 코드 Redis 저장 및 유효시간 관리
 */
package io.why503.companyservice.Sv;

import org.springframework.stereotype.Service;

import io.why503.companyservice.utill.AuthCodeGenerator;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyEmailAuthService {

    private final JavaMailSender mailSender;       // 이메일 발송 처리 컴포넌트
    private final StringRedisTemplate redisTemplate; // 인증 코드 Redis 저장/조회용 Template

    private static final long AUTH_CODE_TTL = 300; // 인증 코드 유효 시간 (초 단위, 5분)

    public void sendAuthCode(String email) {

        String authCode = AuthCodeGenerator.generate(); // 랜덤 인증 코드 생성

        // 인증 코드 Redis 저장 (TTL 기반 자동 만료)
        String key = buildKey(email); // 이메일 기준 Redis Key 생성
        redisTemplate.opsForValue()
            .set(key, authCode, AUTH_CODE_TTL, TimeUnit.SECONDS);

        // 인증 코드 이메일 발송 메시지 구성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("503flowin@gmail.com"); // 발신자 이메일
        message.setTo(email);                  // 수신자 이메일
        message.setSubject("[Company Service] 이메일 인증 코드"); // 이메일 제목
        message.setText(
            "## 사업자 등록증, 인감 증명서 등 서류 제출을 인증 코드로 생략## " +
            "인증 코드: " + authCode + "\n" +
            "인증 코드는 5분 후 만료됩니다."
        );

        mailSender.send(message); // 이메일 발송

        System.out.println("AuthCode for " + email + " = " + authCode); // 콘솔에 이메일 / 인증코드
    }

    private String buildKey(String email) {
        return "company:email:auth:" + email; // 회사 이메일 인증 코드 Redis Key 규칙
    }
}
