package io.why503.accountservice.Auth.Cfg;


import io.why503.accountservice.Auth.Cfg.Filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                .formLogin(form ->form
                        .loginPage("/login")    //로그인 url
                        .loginProcessingUrl("/login")   //post를 보낼 url
                        .successHandler(authenticationSuccessHandler)   //성공핸들러
                        .permitAll()    //로그인은 누구나 접근 가능
                )
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  //서버에서 세션 안만듬, JWT로만 인증
                )
                .authorizeHttpRequests( //허용 url
                        (auth) -> auth
                                .anyRequest().authenticated()
//                                .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
