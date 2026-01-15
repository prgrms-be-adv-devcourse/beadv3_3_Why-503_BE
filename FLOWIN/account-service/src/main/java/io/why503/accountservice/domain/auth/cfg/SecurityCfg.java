package io.why503.accountservice.domain.auth.cfg;


import io.why503.accountservice.domain.auth.cfg.filter.JwtAuthenticationFilter;
import io.why503.accountservice.domain.auth.sv.impl.AuthenticationSuccessHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
접근 설정과 필터 설정을 위한 config
 */
@Configuration
@EnableWebSecurity
public class SecurityCfg {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationSuccessHandlerImpl authenticationSuccessHandler,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception{
        return http
                .csrf((csrf -> csrf.disable()))     //csrf 설정 false
                .cors(Customizer.withDefaults())    //다른 도메인에서 api호출 가능하게
                .formLogin(form ->form  //post를 보낼 url
                        .successHandler(authenticationSuccessHandler)   //성공핸들러
                )
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  //서버에서 세션 안만듬, JWT로만 인증
                )
                .authorizeHttpRequests( //허용 url 리스트
                        (auth) -> auth
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/sign-up").permitAll()
                                .anyRequest().authenticated()
//                                .anyRequest().permitAll() //테스트 할 때 주석을 해제하고 사용
                )
                //필터를 추가하는 함수, 이름과 비밀번호를 검증하는 필터 전에 jwt검증 필터를 넣음
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
