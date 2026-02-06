package io.why503.gatewayservice.auth.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.why503.commonbase.exception.CustomException;
import io.why503.commonbase.model.dto.ExceptionResponse;
import io.why503.gatewayservice.auth.util.exception.AuthForbidden;
import io.why503.gatewayservice.auth.util.exception.AuthUnauthorized;
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

    public Mono<Void> writeUnauthorized(
            ServerWebExchange exchange,
            String message){
        CustomException e = new AuthUnauthorized(message);
        return writeException(exchange, e);
    }

    public Mono<Void> writeForbidden(
            ServerWebExchange exchange,
            String message){
        CustomException e = new AuthForbidden(message);
        return writeException(exchange, e);
    }

    private Mono<Void> writeException(
            ServerWebExchange exchange,
            CustomException e){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(e.getStatus());
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ExceptionResponse bodyDto = new ExceptionResponse(e);
        log.error("{}/{}/{}/{}/{}",
                e.getCause(),
                e.getCode(),
                e.getMessage(),
                e.getClass(),
                e.getUUID());
        byte[] body;
        try {
            body = om.writeValueAsBytes(bodyDto);
        } catch (JsonProcessingException ex) {
            body = "Json error".getBytes(StandardCharsets.UTF_8);
        } catch (Exception ex){
            body = "unknown error".getBytes(StandardCharsets.UTF_8);
        }
        DataBuffer bodyBuffer = response.bufferFactory().wrap(body);
        return response.writeWith(Mono.just(bodyBuffer));
    }
}
