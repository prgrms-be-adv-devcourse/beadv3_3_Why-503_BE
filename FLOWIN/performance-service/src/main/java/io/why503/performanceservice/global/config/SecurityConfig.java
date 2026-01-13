/**
 * Security Configuration
 * Spring Security 기본 보안 설정을 제어하는 설정 클래스
 *
 * 사용 목적 :
 * - 개발 단계에서 인증/인가로 인한 401 오류 방지
 * - POSTMAN 테스트를 원활하게 진행하기 위한 임시 보안 설정
 *
 * 중요 :
 * - 본 설정은 "개발/테스트 단계 전용" 임시 설정
 * - 추후 로그인 기능(JWT, 인증 서버 연동) 도입 시
 *   반드시 제거 또는 수정 예정
 *
 * 배경 :
 * - spring-boot-starter-security 의존성 추가 시
 *   모든 API가 기본적으로 인증 필요 상태가 됨
 * - 별도 설정이 없으면 POST 요청 시 401 Unauthorized 발생
 */
package io.why503.performanceservice.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * Security Filter Chain 설정 (임시)
     *
     * 주의 :
     * - 현재는 모든 요청을 허용하도록 설정
     * - 로그인 / 권한 기능 연동 시 제거 대상
     *
     * 처리 내용 :
     * 1. CSRF 보호 비활성화
     *    - POSTMAN 테스트를 위해 임시 비활성화
     *
     * 2. 모든 요청 허용
     *    - 인증/인가 없이 전체 API 접근 가능
     *
     * 3. 기본 로그인 폼 비활성화
     *    - Spring Security 기본 로그인 페이지 노출 방지
     *
     * 4. HTTP Basic 인증 비활성화
     *    - Authorization 헤더 요구 제거
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // CSRF 보호 비활성화 (개발 단계 임시 설정)
            .csrf(csrf -> csrf.disable())

            // 모든 요청 허용 (로그인 연동 전까지 임시)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )

            // 기본 로그인 폼 비활성화
            .formLogin(form -> form.disable())

            // HTTP Basic 인증 비활성화
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
