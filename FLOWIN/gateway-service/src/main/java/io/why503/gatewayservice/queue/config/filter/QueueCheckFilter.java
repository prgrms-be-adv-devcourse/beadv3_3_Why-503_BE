package io.why503.gatewayservice.queue.config.filter;

import io.why503.gatewayservice.entrytoken.service.EntryTokenIssuer;
import io.why503.gatewayservice.queue.exception.InvalidQueueRequestException;
import io.why503.gatewayservice.queue.exception.QueueWaitingException;
import io.why503.gatewayservice.queue.service.QueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QueueCheckFilter 
        extends AbstractGatewayFilterFactory<QueueCheckFilter.Config>{

    // 대기열 및 active 상태 관리 서비스
    private final QueueService queueService;
    // 입장 자격 발급 서비스
    private final EntryTokenIssuer entryTokenIssuer;

    public static class Config {}

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

            ServerHttpRequest request = exchange.getRequest();

            String userId = request.getHeaders().getFirst("X-USER-SQ");
            String showId = extractShowId(request);

            // 필수 정보 누락
            if (userId == null || showId == null) {
                throw new InvalidQueueRequestException("invalid request");
            }

            // active 수 기준으로 즉시 입장 가능한지 판단
            if (queueService.canEnter(showId, userId)) {
                entryTokenIssuer.issue(showId, userId);
                return chain.filter(exchange);
            }

            // 입장 불가? -> 대기열 진입
            if (!queueService.isAlreadyQueued(showId, userId)) {
                queueService.enqueue(showId, userId);
                log.info("[QUEUE] enqueue userId={} showId={}", userId, showId);
            }

            // 현재 대기열 위치 및 전체 대기 인원 조회
            Long position = queueService.getQueuePosition(showId, userId);
            Long total = queueService.getQueueSize(showId);

            long retryAfter = 3L; // 정책값

            // 대기열 있다면 응답 반환 HTTP 202
            throw new QueueWaitingException(
                    "현재 대기열에 있습니다",
                    position,
                    total,
                    retryAfter
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

}
