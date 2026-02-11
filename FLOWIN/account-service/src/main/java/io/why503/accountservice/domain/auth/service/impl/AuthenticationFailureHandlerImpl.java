package io.why503.accountservice.domain.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.why503.accountservice.domain.auth.util.AuthExceptionFactory;
import io.why503.commonbase.exception.CustomException;
import io.why503.commonbase.model.dto.ExceptionResponse;
import io.why503.commonbase.model.dto.LogResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

    private final ObjectMapper om;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        //응답 기본 설정
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        //로그를 던지기 위한 예외 생성
        CustomException e = AuthExceptionFactory.authUnauthorized(exception.getCause());
        //로그 던지기
        log.error(om.writeValueAsString(new LogResponse(e)));
        //예외로 dto 생성
        ExceptionResponse exceptionResponse = new ExceptionResponse(e);
        //응답 던지기
        response.getWriter().write(om.writeValueAsString(exceptionResponse));
    }
}
