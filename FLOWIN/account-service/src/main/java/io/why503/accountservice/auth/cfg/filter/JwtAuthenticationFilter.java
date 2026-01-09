package io.why503.accountservice.auth.cfg.filter;

import io.why503.accountservice.auth.model.dto.AccountDetails;
import io.why503.accountservice.auth.sv.impl.JwtProviderImpl;
import io.why503.accountservice.auth.sv.impl.UserDetailsSvImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${custom.jwt.cookie-name}")
    private String cookieName;
    @Value("${custom.jwt.id}")
    private String id;


    private final JwtProviderImpl jwtProvider;
    private final UserDetailsSvImpl userDetailsSv;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().equals("/login");
    }

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

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = findCookieString(request);

        if(token == null){
            filterChain.doFilter(request, response);
            return;
        }
        if(!jwtProvider.validate(token)){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid jwt");
            return;
        }

        Map<String, Object> claims = jwtProvider.getClaims(token);

        AccountDetails details = (AccountDetails) userDetailsSv.loadUserByUsername(claims.get(id).toString());

        UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(
                details, null, details.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(upat);

        filterChain.doFilter(request, response);
    }

}
