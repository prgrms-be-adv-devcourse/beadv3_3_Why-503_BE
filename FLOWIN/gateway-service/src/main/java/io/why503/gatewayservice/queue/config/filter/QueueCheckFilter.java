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

    // 대기열 및 active 상태 관리 서비스
    private final QueueService queueService;
    // 입장 자격 발급 서비스
    private final EntryTokenIssuer entryTokenIssuer;
    private final ObjectMapper om = new ObjectMapper();

    public static class Config {
        // Config
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

            // String path = request.getURI().getPath();
            String userId = request.getHeaders().getFirst("X-USER-SQ");
            String showId = extractShowId(request);

            // log.info("[대기열] request path={}, showId={}, userId={}",
                    // path, showId, userId);

            // === 1. 요청 방어 로직 ===

            // 필수 정보 누락
            if (userId == null || showId == null) {
                // log.warn("[대기열] invalid request (missing header or path)");

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


            // === 2. 입장 가능 여부 판단 ===
            // active 수 기준으로 즉시 입장 가능한지 판단
            boolean canEnter = queueService.canEnter(showId, userId);
            // log.info("[대기열] canEnter={}", canEnter);

            if (canEnter) {
                // EntryToken 발급 -> 입장권 + TTL 시작
                entryTokenIssuer.issue(showId, userId);
                
                // log.info("[대기열] entry token issued showId{}, userId{}",
                    // showId, userId);

                // Entry필터로 전달
                return chain.filter(exchange);
            }


            // === 3. 입장 불가 시 대기열 처리 ===
            // 입장 불가? -> 대기열 진입하쇼
            if (!queueService.isAlreadyQueued(showId, userId)) {
                queueService.enqueue(showId, userId);
                log.info("[대기열] enqueue userId={} into showId={}",
                        userId, showId);
            } else {
                log.info("[대기열] already queued userId={} showId={}",
                        userId, showId);
            }

            // 현재 대기열 위치 및 전체 대기 인원 조회
            Long position = queueService.getQueuePosition(showId, userId);
            Long total = queueService.getQueueSize(showId);

            QueueRejectResponseBody body =
                    new QueueRejectResponseBody(
                    "현재 대기열에 있습니다",
                            position,
                            total
        );
            // 대기열 있다면 응답 반환 HTTP 429 
            return response.writeWith(
                    Flux.just(writeResponse(
                            response,
                            HttpStatus.TOO_MANY_REQUESTS,
                            body
                    ))
    );
        };
    }

    // 요청 경로에 showId 추출
    // 경로 형식 : performance/{showId}/entry
    private String extractShowId(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        String[] parts = path.split("/");

        if (parts.length < 3) {
            return null;
        }
        return parts[2];
    }

    // reject 응답 생성
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

    // 응답을 Json byte[]러 직렬화
    private byte[] toBytes(QueueRejectResponseBody body) {
        try {
            return om.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            log.error("[대기열] failed to serialize response body", e);
            throw new RuntimeException(e);
        }
    }
}
