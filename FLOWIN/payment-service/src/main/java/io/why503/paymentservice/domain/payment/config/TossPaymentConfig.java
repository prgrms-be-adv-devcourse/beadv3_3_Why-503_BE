package io.why503.paymentservice.domain.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TossPaymentConfig {

    @Value("${toss.secret-key}") // application.yml의 값을 읽어오도록 수정
    private String tossSecretKey;

    @Bean
    public RestTemplate tossRestTemplate() {
        return new RestTemplate();
    }

    public String getSecretKey() {
        return tossSecretKey;
    }
}