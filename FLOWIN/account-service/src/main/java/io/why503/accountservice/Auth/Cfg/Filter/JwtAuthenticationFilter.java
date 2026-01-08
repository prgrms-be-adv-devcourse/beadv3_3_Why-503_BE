package io.why503.accountservice.Auth.Cfg.Filter;

import io.why503.accountservice.Auth.Model.Dto.AccountDetails;
import io.why503.accountservice.Auth.Sv.Impl.JwtProviderImpl;
import io.why503.accountservice.Auth.Sv.Impl.UserDetailsSvImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

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
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if(request.getCookies() == null){
            throw new AuthenticationCredentialsNotFoundException("JWT cookie is not exist");
        }

        String token = Arrays.stream(request.getCookies())
                .filter((i) -> i.getName().equals(cookieName))
                .map((i) -> i.getValue())
                .findFirst()
                .orElse(null);

        if(token == null){
            throw new AuthenticationCredentialsNotFoundException("JWT cookie is not exist");
        }
        if(token.isBlank()){
            throw new AuthenticationCredentialsNotFoundException("JWT is blank");
        }
        if(!jwtProvider.validate(token)){
            throw new AuthenticationCredentialsNotFoundException("JWT is invalid");
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
