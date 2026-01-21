package io.why503.gatewayservice.auth.config.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.why503.gatewayservice.auth.model.dto.JwtAuthResponseBody;
import io.why503.gatewayservice.auth.model.dto.TokenBody;
import io.why503.gatewayservice.auth.service.JwtValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Slf4j
@Component 
public class JwtValidationFilter 
        extends AbstractGatewayFilterFactory<JwtValidationFilter.Config> {
    //쿠키 이름
    @Value("${custom.jwt.cookie-name}")
    private String cookieName;
    //jwt 검증기
    private final JwtValidator jwtValidator;
    //json 만들기 위해서 생성
    private final ObjectMapper om = new ObjectMapper();
    //AbstractGatewayFilterFactory특성 상 필터를 추가 하기 위해서 필요함
    public static class Config{

    }
    /*
    생성자
    특성상 @Builder을 못쓰기에 명시적으로 초기화
     */
    public JwtValidationFilter(JwtValidator jwtValidator){
        super(Config.class);
        this.jwtValidator = jwtValidator;
    }
    //필터
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            //exchange => 응답 + 요청 섞인 거
            //chain => 필터체인
            ServerHttpResponse response = exchange.getResponse();//응답
            ServerHttpRequest request = exchange.getRequest();//요청
            //1차 검증, 일단 쿠키가 없으면 401반환 로그인 x라는 뜻
            if(!request.getCookies().containsKey(cookieName)){
                String message = "token or cookie is empty";
                log.info(message);

                return response.writeWith(
                        Flux.just(writeToResponseByHttpStatus(response, HttpStatus.UNAUTHORIZED, message))
                );
            }
            //요청의 쿠키에서 jwt토큰을 꺼냅
            Optional<String> tokenOptional = pullTokenOfRequest(request);
            //Optional을 풀어주는 절차
            String tokenString = tokenOptional.orElseThrow(
                    () -> new IllegalArgumentException("token is empty")
            );
            //2차 검증, 이 곳에서 걸리는 토큰은 위변조된 토큰, 401에러
            if(!jwtValidator.validate(tokenString)){
                String message = "invalid token";
                log.info(message);

                return response.writeWith(
                        Flux.just(writeToResponseByHttpStatus(response, HttpStatus.UNAUTHORIZED, message))
                );
            }
            /*
            토큰을 파싱해서 sq만 저장
             */
            TokenBody body = jwtValidator.parse(tokenString);

            //이후 요청 헤더에 sq를 올림
            ServerHttpRequest requestMutated = request.mutate()
                    .header("X-USER-SQ", body.sq().toString())
                    .build();
            //다음 체인으로 넘김
            return chain.filter(exchange.mutate().request(requestMutated).build());
        });
    }
    /*
    DataBuffer는 byte[]를 안전하게 운송하기 위한 클래스
    응답, http코드, 메시지를 담아서 오류 상황시 결합해서 반환
     */
    private DataBuffer writeToResponseByHttpStatus(
            ServerHttpResponse response,
            HttpStatus status,
            String message
    ){
        response.setStatusCode(status); //입력받은 http 코드
        //json이라는 것을 명시
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        JwtAuthResponseBody body = new JwtAuthResponseBody(message); //메시지 body
        return response.bufferFactory().wrap(jwtAuthResponseBodyToBytes(body));
    }
    //ObjectMapper을 이용해서 class -> json -> byte[]로 변환
    private byte[] jwtAuthResponseBodyToBytes(JwtAuthResponseBody body){
        try{
            return om.writeValueAsBytes(body);
        }
        catch (JsonProcessingException e){
            log.error("Failed to serialize response body", e);
            throw new RuntimeException(e);
        }
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
