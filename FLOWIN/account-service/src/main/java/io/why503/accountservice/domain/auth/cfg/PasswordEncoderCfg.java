package io.why503.accountservice.domain.auth.cfg;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/*
PasswordEncoder를 BCrypt로 하겠다고 spring에 명시
그리고 PasswordEncoder를 빈으로 올리기 위한 config
 */
@Configuration
public class PasswordEncoderCfg {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
