package io.why503.performanceservice.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "io.why503.performanceservice") // 패키지 경로 지정
public class FeignConfig {

}