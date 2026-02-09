package io.why503.paymentservice.domain.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 토스페이먼츠 연동에 필요한 인증 정보 및 통신 설정 관리
 */
@Configuration
public class TossPaymentConfig {

    @Value("${toss.client.secret-key}")
    private String tossSecretKey;

    @Value("${toss.client.client-key}")
    private String tossClientKey;

    // 외부 결제 API 호출을 위한 공통 통신 객체 생성
    @Bean
    public RestTemplate tossRestTemplate() {
        return new RestTemplate();
    }

    public String getSecretKey() {
        return tossSecretKey;
    }

    public String getClientKey() {
        return tossClientKey;
    }
}