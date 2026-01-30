// queue는 관리하고
// entrytoken은 입장권을 관리

package io.why503.gatewayservice.queue.config.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.why503.gatewayservice.entrytoken.service.EntryTokenIssuer;
import io.why503.gatewayservice.queue.model.dto.QueueRejectResponseBody;
import io.why503.gatewayservice.queue.service.QueueService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

import org.springframework.core.io.buffer.DataBuffer;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class QueueCheckFilter 
        extends AbstractGatewayFilterFactory<QueueCheckFilter.Config>{

    private final QueueService queueService;
    private final EntryTokenIssuer entryTokenIssuer;
    private final ObjectMapper om = new ObjectMapper();

    public static class Config {
        // 앞 쪽은 다음에 탐색하고 오자
    }

    public QueueCheckFilter(
        QueueService queueService,
        EntryTokenIssuer entryTokenIssuer
    ) {
        super(Config.class);
        this.queueService = queueService;
        this.entryTokenIssuer = entryTokenIssuer;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            ServerHttpResponse response = exchange.getResponse();
            ServerHttpRequest request = exchange.getRequest();

            String path = request.getURI().getPath();
            String userId = request.getHeaders().getFirst("X-USER-SQ");
            String showId = extractShowId(request);

            log.info("[QUEUE] request path={}, showId={}, userId={}",
                    path, showId, userId);

            // === 방어 로직 ===
            if (userId == null || showId == null) {
                log.warn("[QUEUE] invalid request (missing header or path)");

                QueueRejectResponseBody body =
                        new QueueRejectResponseBody(
                        "invalid request",
                        null,
                        null
                        );
                return response.writeWith(
                        Flux.just(writeResponse(
                                response,
                                HttpStatus.BAD_REQUEST,
                                body
                        ))
                );
            }


            // === 입장 가능 여부 판단 ===
            boolean canEnter = queueService.canEnter(showId, userId);
            log.info("[QUEUE] canEnter={}", canEnter);

            if (canEnter) {
                // EntryToken 발급 -> 입장권 + TTL 시작
                entryTokenIssuer.issue(showId, userId);
                log.info("[QUEUE] entry token issued showId{}, userId{}",
                    showId, userId);
                return chain.filter(exchange);
            }

            // 입장 불가? -> 대기열 진입하쇼
            if (!queueService.isAlreadyQueued(showId, userId)) {
                queueService.enqueue(showId, userId);
                log.info("[QUEUE] enqueue userId={} into showId={}",
                        userId, showId);
            } else {
                log.info("[QUEUE] already queued userId={} showId={}",
                        userId, showId);
            }

            Long position = queueService.getQueuePosition(showId, userId);
            Long total = queueService.getQueueSize(showId);

            QueueRejectResponseBody body =
                    new QueueRejectResponseBody(
                    "현재 대기열에 있습니다",
                            position,
                            total
        );

            return response.writeWith(
                    Flux.just(writeResponse(
                            response,
                            HttpStatus.TOO_MANY_REQUESTS,
                            body
                    ))
    );
        };
    }

    // /performance/{showId}/entry
    private String extractShowId(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        String[] parts = path.split("/");

        if (parts.length < 3) {
            return null;
        }
        return parts[2];
    }

    private DataBuffer writeResponse(
            ServerHttpResponse response,
            HttpStatus status,
            QueueRejectResponseBody body
    ) {
        response.setStatusCode(status);
        response.getHeaders().add(
            HttpHeaders.CONTENT_TYPE, 
            "application/json");

        return response.bufferFactory().wrap(toBytes(body));
    }

    private byte[] toBytes(QueueRejectResponseBody body) {
        try {
            return om.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            log.error("[QUEUE] failed to serialize response body", e);
            throw new RuntimeException(e);
        }
    }
}
