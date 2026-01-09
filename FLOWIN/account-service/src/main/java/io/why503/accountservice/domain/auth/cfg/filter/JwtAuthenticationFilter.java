package io.why503.accountservice.domain.auth.cfg.filter;

import io.why503.accountservice.domain.auth.model.dto.AccountDetails;
import io.why503.accountservice.domain.auth.sv.impl.JwtProviderImpl;
import io.why503.accountservice.domain.auth.sv.impl.AccountDetailsSvImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    //AccountDetail id변수 이름
    @Value("${custom.jwt.id}")
    private String id;


    private final JwtProviderImpl jwtProvider;
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
        Map<String, Object> claims = jwtProvider.getClaims(token);
        //payload를 AccountDetails로 변환
        AccountDetails details = accountDetailsSv.loadUserByUsername(claims.get(id).toString());
        //다음 필터에 넘길 토큰 생성
        UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(
                details, null, details.getAuthorities()
        );
        //인증 되었다는 걸 토큰과 함께 컨텍스트에 태움
        SecurityContextHolder.getContext().setAuthentication(upat);
        //다음 토큰으로
        filterChain.doFilter(request, response);
    }

}
