package io.why503.accountservice.domain.auth.config;

import io.why503.accountservice.domain.auth.service.impl.AuthenticationSuccessHandlerImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/*
접근 설정과 필터 설정을 위한 config
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${custom.jwt.cookie-name}")
    private String cookieName;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationSuccessHandlerImpl authenticationSuccessHandler) throws Exception{
        return http
                .csrf((csrf -> csrf.disable()))     //csrf 설정 false
                .cors(Customizer.withDefaults())    //다른 도메인에서 api호출 가능하게
                .formLogin(form ->form  //post를 보낼 url
                        .loginPage("/auth/login")    //로그인 페이지
                        .loginProcessingUrl("/auth/login") //post보낼 위치
                        .successHandler(authenticationSuccessHandler)   //성공핸들러
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout") //로그아웃 url
                        .deleteCookies(cookieName) //삭제할 쿠키
                        .logoutSuccessHandler( //로그아웃 성공시 핸들러
                                (request, response, authentication) -> {
                                    expireCookie(response, cookieName); //하단 참조
                                    response.setStatus(HttpServletResponse.SC_OK); //성공 시 반환할 Http 코드
                                }
                        )
                )
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  //서버에서 세션 안만듬, JWT로만 인증
                )
                .authorizeHttpRequests( //허용 url 리스트
                        (auth) -> auth
                                .anyRequest().permitAll()
                )
                .build();
    }
    //쿠키 확실하게 삭제하는 함수
    private void expireCookie(HttpServletResponse response, String findCookieName){
        Cookie cookie = new Cookie(findCookieName, null);

        //쿠키 속성
        cookie.setMaxAge(0); // 쿠키 만료 시간
        cookie.setHttpOnly(true); //  js는 접근 불가
        //cookie.setSecure(true); // https에서만 사용
        cookie.setSecure(false);
        cookie.setPath("/"); //모든 경로 가능

        response.addCookie(cookie);
    }
}