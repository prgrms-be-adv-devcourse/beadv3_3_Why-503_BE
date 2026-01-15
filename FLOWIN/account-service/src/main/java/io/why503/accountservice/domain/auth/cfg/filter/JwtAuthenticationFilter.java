package io.why503.accountservice.domain.auth.cfg.filter;

import io.why503.accountservice.domain.auth.model.dto.AccountDetails;
import io.why503.accountservice.domain.auth.model.dto.TokenBody;
import io.why503.accountservice.domain.auth.sv.JwtProvider;
import io.why503.accountservice.domain.auth.sv.impl.JwtProviderImpl;
import io.why503.accountservice.domain.auth.sv.impl.AccountDetailsSvImpl;
import io.why503.accountservice.util.UserRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/*
JWT확인하는 필터, UsernamePassword필터 앞에 설치
JWT확인 -> payload를 AccountDetail로 만듬 -> 다음필터에 정보를 넘김
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //cookie 이름 yml파일 명시
    @Value("${custom.jwt.cookie-name}")
    private String cookieName;



    private final JwtProvider jwtProvider;
    private final AccountDetailsSvImpl accountDetailsSv;

    //login접근에는 필터 x
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().equals("/login");
    }
    //받은 요청에서 cookieName인 쿠키를 찾아 jwt를 꺼냄
    private String findCookieString(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            return null;
        }
        for(Cookie i : cookies){
            if(i.getName().equals(cookieName)){
                return i.getValue();
            }
        }
        return null;
    }

    //필터 본문
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        //쿠키 찾음
        String token = findCookieString(request);
        //쿠키가 없으면 인증전 -> 필터를 넘기면 알아서 /login으로
        if(token == null){
            filterChain.doFilter(request, response);
            return;
        }
        //jwt검즘, 위변조가 되었다면 error 던짐
        if(!jwtProvider.validate(token)){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid jwt");
            return;
        }
        //jwt의 payload를 변수에 저장
        TokenBody tokenBody = jwtProvider.parse(token);
        //payload를 AccountDetails로 변환
        AccountDetails details = new AccountDetails(
                null,
                null,
                tokenBody.sq(),
                tokenBody.name(),
                tokenBody.role()
        );
        //가짜 헤더
        UserRequestWrapper wrapped = new UserRequestWrapper(request);
        //헤더에 추가
        wrapped.addHeader("X-USER-SQ", tokenBody.sq().toString());
        wrapped.addHeader("X-USER-NAME", tokenBody.name());
        wrapped.addHeader("X-USER-ROLE", tokenBody.role().getCode().toString());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        details,
                        null,
                        details.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //다음 토큰으로
        filterChain.doFilter(wrapped, response);
        //filterChain.doFilter(request, response); //나중에 gateway 추가 시 부활
    }

}
