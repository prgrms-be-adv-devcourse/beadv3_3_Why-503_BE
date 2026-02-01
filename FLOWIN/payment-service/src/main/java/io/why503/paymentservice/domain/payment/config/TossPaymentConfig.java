package io.why503.paymentservice.domain.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 외부 결제 시스템 연동을 위한 설정값 로드 및 빈 등록 클래스
 */
@Configuration
public class TossPaymentConfig {

    @Value("${toss.client.secret-key}")
    private String tossSecretKey;

    @Value("${toss.client.client-key}")
    private String tossClientKey;

    // 외부 API 통신을 위한 RestTemplate 빈 등록
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