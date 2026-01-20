package io.why503.paymentservice.domain.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TossPaymentConfig {

    @Value("toss_test_key")
    private String tossSecretKey;

    @Bean
    public RestTemplate tossRestTemplate() {
        return new RestTemplate();
    }

    public String getSecretKey() {
        return tossSecretKey;
    }
}
