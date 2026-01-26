package io.why503.paymentservice.global.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest; // Spring Boot 3.x (2.x라면 javax.servlet...)

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attributes =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    // 현재 요청의 헤더에서 'Authorization' (JWT 토큰)을 꺼냄
                    String authorization = request.getHeader("Authorization");

                    // 토큰이 있다면 Feign 요청 헤더에 그대로 꽂아줌
                    if (authorization != null) {
                        template.header("Authorization", authorization);
                    }

                    // (선택 사항) 아까 동료들이 말했던 'X-USER-SQ'도 여기서 자동으로 넣을 수 있습니다.
                    // 이렇게 하면 PerformanceClient 메서드에서 @RequestHeader를 지워도 됩니다.
                    /*
                    String userSq = request.getHeader("X-USER-SQ");
                    if (userSq != null) {
                        template.header("X-USER-SQ", userSq);
                    }
                    */
                }
            }
        };
    }
}