package io.why503.paymentservice.domain.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 토스 페이먼츠 설정
 * - application.yml의 설정값을 로드하고, 통신에 필요한 Bean을 등록합니다.
 */
@Configuration
public class TossPaymentConfig {

    @Value("${toss.client.secret-key}")
    private String tossSecretKey;

    @Value("${toss.client.client-key}")
    private String tossClientKey;

    // 외부 API 호출을 위한 RestTemplate 등록
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