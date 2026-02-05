package io.why503.gatewayservice.auth.config.filter;

import io.why503.gatewayservice.auth.model.dto.TokenBody;
import io.why503.gatewayservice.auth.service.JwtValidator;
import io.why503.gatewayservice.auth.util.exception.AuthUnauthorized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Slf4j
@Component 
public class JwtValidationFilter 
        extends AbstractGatewayFilterFactory<JwtValidationFilter.Config> {
    
    @Value("${custom.jwt.cookie-name}") // 쿠키 이름
    private String cookieName;

    private final JwtValidator jwtValidator; // jwt 검증기

    // AbstractGatewayFilterFactory 특성상 필터를 추가 하기 위해서 필요함
    public static class Config{}

    // 생성자 특성상 @Builder을 못쓰기에 명시적으로 초기화
    public JwtValidationFilter(JwtValidator jwtValidator){
        super(Config.class);
        this.jwtValidator = jwtValidator;
    }

    // 필터
    @Override
    public GatewayFilter apply(Config config) {
        // exchange => 응답 + 요청 섞인 거
        // chain => 필터체인
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();//요청

            // 1차 검증, 일단 쿠키가 없으면 401반환 로그인 x라는 뜻
            if(!request.getCookies().containsKey(cookieName)){
                log.info("token or cookie is empty");
                throw new AuthUnauthorized("token or cookie is empty"); 
            }

            // Optional을 풀어주는 절차
            String token = pullTokenOfRequest(request)
                    .orElseThrow(() -> 
                            new IllegalArgumentException("token is empty")
                    );

            // 2차 검증, 이 곳에서 걸리는 토큰은 위변조된 토큰, 401에러
            if(!jwtValidator.validate(token)){
                log.info("invalid token");
                throw new AuthUnauthorized("invalid token");
            }

            // 토큰을 파싱해서 sq만 저장
            TokenBody body = jwtValidator.parse(token);

            // 이후 요청 헤더에 sq를 올림
            ServerHttpRequest requestMutated = request.mutate()
                    .header("X-USER-SQ", body.sq().toString())
                    .build();
            //다음 체인으로 넘김
            return chain.filter(exchange.mutate().request(requestMutated).build());
        });
    }

    //쿠키에서 jwt를 꺼냄
    private Optional<String> pullTokenOfRequest(ServerHttpRequest request){
        HttpCookie cookie = request.getCookies().getFirst(cookieName);
        if(cookie == null){
            return Optional.empty();
        }
        return Optional.of(cookie.getValue());
    }
}
