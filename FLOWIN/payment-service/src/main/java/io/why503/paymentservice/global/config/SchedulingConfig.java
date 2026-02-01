package io.why503.paymentservice.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 애플리케이션의 스케줄링 기능을 활성화하는 설정 클래스
 */
@Configuration
@EnableScheduling
@Profile("!test")
public class SchedulingConfig {
}