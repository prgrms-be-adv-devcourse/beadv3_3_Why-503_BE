package io.why503.gatewayservice.queue.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QueueConfig
 * - 대기열 관련 설정 값을 애플리케이션 시작 시 한 번만 초기화
 * - QueueService / Filter에서 공통으로 사용
 */ 
@Configuration
public class QueueConfig {

    @Value("${custom.queue.max-active}")
    private int maxActiveProperty;

    private int maxActive;

    /**
     * Bean 생성 이후 1회 실행
     */
    @PostConstruct
    private void init() {
        this.maxActive = maxActiveProperty;
    }

    /**
     * 최대 동시 입장 가능 인원 Bean
     */
    @Bean
    public Integer maxActive() {
        return maxActive;
    }
}
