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

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    private static final long AUTH_CODE_TTL = 300;

    public void sendAuthCode(String email) {

        String authCode = AuthCodeGenerator.generate();

        // 1. Redis 저장
        String key = buildKey(email);
        redisTemplate.opsForValue()
            .set(key,authCode,AUTH_CODE_TTL,TimeUnit.SECONDS);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("503flowin@gmail.com");
        message.setTo(email);
        message.setSubject("[Company Service] 이메일 인증 코드");
        message.setText(
            "## 사업자 등록증, 인감 증명서 등 서류 제출을 인증 코드로 생략## " +
            "인증 코드: " + authCode + "\n" +
            "인증 코드는 5분 후 만료됩니다."
        );

        mailSender.send(message);

        System.out.println("AuthCode for " + email + " = " + authCode);
    }

    private String buildKey(String email) {
        return "company:email:auth:" + email;
    }
}