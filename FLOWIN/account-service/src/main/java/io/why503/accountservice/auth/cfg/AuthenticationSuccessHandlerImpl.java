package io.why503.accountservice.auth.cfg;

import io.why503.accountservice.auth.model.dto.AccountDetails;
import io.why503.accountservice.auth.sv.impl.JwtProviderImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private final JwtProviderImpl jwtProvider;

    @Value("${custom.jwt.valid-time-ms}")
    private Long validTime;

    @Value("${custom.jwt.cookie-name}")
    private String cookieName;
    @Value("${custom.jwt.sq}")
    private String sq;
    @Value("${custom.jwt.id}")
    private String id;
    @Value("${custom.jwt.name}")
    private String name;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        AccountDetails details = (AccountDetails) authentication.getPrincipal();

        Map<String, Object> body = new HashMap<>();

        body.put("user_sq", details.getSq());
        body.put("user_id", details.getUsername());
        body.put("user_name", details.getName());

        String token = jwtProvider.issue(validTime , body);
        Cookie cookie = new Cookie(cookieName, token);

        cookie.setMaxAge((int)(validTime / 1000)); // 쿠키 만료 시간
        cookie.setHttpOnly(true); //  js는 접근 불가
        //cookie.setSecure(true); // https에서만 사용
        cookie.setSecure(false);
        cookie.setPath("/"); //모든 경로 가능

        response.addCookie(cookie);
    }
}
