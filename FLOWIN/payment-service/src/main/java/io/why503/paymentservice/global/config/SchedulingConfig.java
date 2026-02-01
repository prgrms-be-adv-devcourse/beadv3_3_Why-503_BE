package io.why503.paymentservice.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Profile("!test")
public class SchedulingConfig {
    // 나중에 스케줄러 스레드 풀 설정 등을 여기서 관리할 수 있어 확장성도 좋습니다.
}