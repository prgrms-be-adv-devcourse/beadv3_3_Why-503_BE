package io.why503.gatewayservice.auth.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.why503.commonbase.exception.CustomException;
import io.why503.commonbase.model.dto.ExceptionResponse;
import io.why503.gatewayservice.auth.util.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayExceptionWriter {

    private final ObjectMapper om;

    //Unauthorized(401)
    public Mono<Void> writeUnauthorized(
            ServerWebExchange exchange,
            String message){
        CustomException e = new AuthUnauthorized(message);
        return writeException(exchange, e);
    }

    //Forbidden(403)
    public Mono<Void> writeForbidden(
            ServerWebExchange exchange,
            String message){
        CustomException e = new AuthForbidden(message);
        return writeException(exchange, e);
    }

    //NotFound(404)
    public Mono<Void> writeNotFound(
            ServerWebExchange exchange,
            String message){
        CustomException e = new AuthNotFound(message);
        return writeException(exchange, e);
    }

    //InternalServerError(500)
    public Mono<Void> writeInternalServerError(
            ServerWebExchange exchange,
            String message){
        CustomException e = new AuthInternalServerError(message);
        return writeException(exchange, e);
    }

    //BadGateway(502)
    public Mono<Void> writeBadGateway(
            ServerWebExchange exchange,
            String message){
        CustomException e = new AuthBadGateway(message);
        return writeException(exchange, e);
    }

    //ServerUnavailable(503)
    public Mono<Void> writeServerUnavailable(
            ServerWebExchange exchange,
            String message){
        CustomException e = new AuthServerUnavailable(message);
        return writeException(exchange, e);
    }

    //GatewayTimeout(504)
    public Mono<Void> writeGatewayTimeout(
            ServerWebExchange exchange,
            String message){
        CustomException e = new AuthGatewayTimeout(message);
        return writeException(exchange, e);
    }

    //에러 코드 log찍고 http 응답으로 내리기
    private Mono<Void> writeException(
            ServerWebExchange exchange,
            CustomException e){
        //http 응답에 기본 구조(httpStatus, 기본 json 통신 명시)
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(e.getStatus());
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        //dto 생성 및 로그 찍기
        ExceptionResponse bodyDto = new ExceptionResponse(e);
        log.error("{}/{}/{}/{}/{}",
                e.getCause(),
                e.getCode(),
                e.getMessage(),
                e.getClass(),
                e.getUUID());
        //dto 파싱, om사용
        byte[] body;
        try {
            body = om.writeValueAsBytes(bodyDto);
        } catch (JsonProcessingException ex) {
            body = "Json error".getBytes(StandardCharsets.UTF_8);
        } catch (Exception ex){
            body = "unknown error".getBytes(StandardCharsets.UTF_8);
        }
        //특성상 DataBuffer를 한번 두르고 응답에 넣어줘야 함
        DataBuffer bodyBuffer = response.bufferFactory().wrap(body);
        return response.writeWith(Mono.just(bodyBuffer));
    }
}
