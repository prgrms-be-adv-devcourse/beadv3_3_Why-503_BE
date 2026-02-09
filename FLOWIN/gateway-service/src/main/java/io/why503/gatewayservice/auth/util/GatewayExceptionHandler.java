package io.why503.gatewayservice.auth.util;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.PrematureCloseException;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.util.concurrent.TimeoutException;

@Order(-1)
@Component
@RequiredArgsConstructor
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private final GatewayExceptionWriter writer;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        //응답이 핸들러를 이미 지나간 상태 -> 그냥 에러째로 다음 핸들러에 넘김
        if(exchange.getResponse().isCommitted()){
            return Mono.error(ex);
        }
        //서비스의 url을 찾을 수 없는 경우(404)
        if(ex instanceof NotFoundException){
            return writer.writeNotFound(exchange, "Service is not found");
        }
        //응답을 받긴 했는데 깨짐(502) -> 연결불량
        if(ex instanceof PrematureCloseException || ex instanceof DecoderException){
            return writer.writeBadGateway(exchange, "Service is not found");
        }
        //모종의 이유로 접근불가(503)
        if(ex instanceof NoRouteToHostException || ex instanceof ConnectException){
            return writer.writeServerUnavailable(exchange, "Service Unavailable");
        }
        //타임아웃(504) -> 일정시간 응답이 안 옴
        if(ex instanceof TimeoutException || ex instanceof ReadTimeoutException){
            return writer.writeGatewayTimeout(exchange, "Gateway Timeout");
        }
        //나머지 500 -> 내부 문제인 듯
        return writer.writeInternalServerError(exchange, "Gateway error");
    }
}
