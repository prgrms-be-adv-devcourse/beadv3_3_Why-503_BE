package io.why503.gatewayservice.entrytoken.config.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.why503.gatewayservice.queue.model.dto.QueueRejectResponseBody;
import io.why503.gatewayservice.entrytoken.exception.EntryTokenRequiredException;
import io.why503.gatewayservice.entrytoken.exception.InvalidEntryRequestException;
import io.why503.gatewayservice.entrytoken.service.EntryTokenValidator;
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

/**
 * Queue 통과 후 발급받은 EntryToken이 없으면 예매 API 접근 차단
 * - JwtValidationFilter가 먼저 실행되어 X-USER-SQ 헤더가 존재
 * - QueueCheckFilter가 통과 시 EntryTokenIssuer.issue() 수행
 */
@Slf4j
@Component
public class EntryTokenFilter
        extends AbstractGatewayFilterFactory<EntryTokenFilter.Config> {

    private final EntryTokenValidator entryTokenValidator;

    public static class Config {}

    public EntryTokenFilter(EntryTokenValidator entryTokenValidator) {
        super(Config.class);
        this.entryTokenValidator = entryTokenValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();

            String userId = request.getHeaders().getFirst("X-USER-SQ");
            // URL path에서 공연 식별자 추출
            String showId = extractShowId(request);

            // 필수 정보 누락시 잘못된 요청 때리기
            if (userId == null || showId == null) {
                throw new InvalidEntryRequestException("invalid request");
            }

            if (!entryTokenValidator.isValid(showId, userId)) {
                throw new EntryTokenRequiredException("entry token required");
            }

            // 실제 서비스로 전달
            return chain.filter(exchange);
        };
    }

    // showId 추출
    private String extractShowId(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        String[] parts = path.split("/");

        // ex: /performances/{showId}/entry
        if (parts.length >= 4 && "performance".equals(parts[1])) {
            return parts[2];
        }
        return null;
    }
}
