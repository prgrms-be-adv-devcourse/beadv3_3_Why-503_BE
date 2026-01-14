package io.why503.accountservice.domain.auth.sv.impl;

import io.why503.accountservice.domain.auth.model.dto.AccountDetails;
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
/*
로그인에 성공했을 경우의 핸들러
 */
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private final JwtProviderImpl jwtProvider;

    @Value("${custom.jwt.valid-time-ms}")
    private Long validTime; //인증 유효시간 yml에 명시

    @Value("${custom.jwt.cookie-name}")
    private String cookieName; //jwt가 있는 쿠키이름 yml에 명시

    //성공했을 경우의 함수
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        //성공 후 만들어져 넘어온 AccountDetails
        AccountDetails details = (AccountDetails) authentication.getPrincipal();

        //jwt에 넣을 payload
        Map<String, Object> body = new HashMap<>();

        body.put("sq", details.getSq());
        body.put("name", details.getName());
        body.put("role", details.getRole().getCode());

        //jwt 생성
        String token = jwtProvider.issue(validTime , body);

        //쿠키에 jwt에 넣음
        Cookie cookie = new Cookie(cookieName, token);

        //쿠키 속성
        cookie.setMaxAge((int)(validTime / 1000)); // 쿠키 만료 시간
        cookie.setHttpOnly(true); //  js는 접근 불가
        //cookie.setSecure(true); // https에서만 사용
        cookie.setSecure(false);
        cookie.setPath("/"); //모든 경로 가능

        //응답에 쿠키를 넣음
        response.addCookie(cookie);
    }
}
