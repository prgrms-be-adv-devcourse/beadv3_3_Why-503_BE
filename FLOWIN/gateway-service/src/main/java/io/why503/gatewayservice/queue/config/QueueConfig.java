package io.why503.gatewayservice.queue.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 대기열 관련 설정 값을 애플리케이션 시작 시 한 번만 초기화
@Configuration
public class QueueConfig {

    @Value("${custom.queue.max-active}")
    private int maxActive;

    @Bean
    public Integer maxActive() {
        return maxActive;
    }

}
